package net.solapps.dashclock.gmail;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.solapps.dashclock.notification.service.IModelFactory;
import android.app.Notification;
import android.service.notification.StatusBarNotification;
import android.widget.RemoteViews;

public class GmailModelFactory implements IModelFactory<GmailModel> {

    private static final Pattern PATTERN = Pattern.compile("\\d+");

    @Override
    public GmailModel createFrom(List<StatusBarNotification> sbns) {
        GmailModel model = new GmailModel().packageName("com.google.android.gm");
        if (sbns.size() == 1) {
            StatusBarNotification sbn = sbns.get(0);
            Notification notification = sbn.getNotification();
            String subject = parseSubject(notification);
            int unread = parseNumber(notification);
            if (subject != null || unread == 0) {
                // subject availability means we have a single message notification
                unread = 1;
            }
            model.fromOrMessage(notification.tickerText != null ? notification.tickerText.toString() : null);
            model.unread(unread).subject(subject).contentIntent(notification.contentIntent);
        } else {
            int totalUnread = 0;
            for (StatusBarNotification sbn : sbns) {
                Notification notification = sbn.getNotification();
                int accountUnread = parseNumber(notification);
                totalUnread += accountUnread > 0 && parseSubject(notification) == null ? accountUnread : 1;
            }
            model.unread(totalUnread);
        }

        return model;
    }

    private int parseNumber(Notification n) {
        int result = 0;
        if (n.tickerText != null) {
            String fromOrMessage = n.tickerText.toString();
            Matcher matcher = PATTERN.matcher(fromOrMessage);
            if (matcher.find()) {
                try {
                    result = Integer.parseInt(matcher.group());
                } catch (NumberFormatException e) {
                    // oops, this should not have happened
                }
            }
        }
        return result;
    }

    private String parseSubject(Notification n) {
        RemoteViews contentView = n.contentView;
        try {
            int fields = 0;
            String subject = null;
            Field field = contentView.getClass().getDeclaredField("mActions");
            field.setAccessible(true);
            @SuppressWarnings("rawtypes")
            List actions = (List) field.get(contentView);
            for (Object action : actions) {
                Field typeField;
                try {
                    typeField = action.getClass().getDeclaredField("type");
                } catch (NoSuchFieldException e) {
                    continue;
                }
                typeField.setAccessible(true);
                if (10 /* CHAR_SEQUENCE */== typeField.getInt(action) && fields++ == 1) {
                    Field valueField = action.getClass().getDeclaredField("value");
                    valueField.setAccessible(true);
                    CharSequence sequence = (CharSequence) valueField.get(action);
                    if (sequence != null) {
                        subject = sequence.toString();
                    }
                }
            }
            if (fields > 3) {
                return subject;
            }
        } catch (Exception e) {
            // Ignored
        }
        return null;
    }
}