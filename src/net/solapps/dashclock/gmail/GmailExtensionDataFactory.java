package net.solapps.dashclock.gmail;

import net.solapps.dashclock.notification.service.IExtensionDataFactory;
import net.solapps.dashclock.notification.ui.ProxyActivity;
import net.solapps.dashclock.gmail.ui.SettingsActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.apps.dashclock.api.ExtensionData;

public class GmailExtensionDataFactory implements IExtensionDataFactory<GmailModel> {

    private Context mContext;
    private SharedPreferences mPrefs;

    public GmailExtensionDataFactory(Context context) {
        mContext = context.getApplicationContext();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public ExtensionData createFrom(GmailModel model) {
        ExtensionData data = new ExtensionData();
        data.clickIntent(new Intent(mContext, ProxyActivity.class)).icon(R.drawable.ic_gmail);
        if (model.unread() > 0) {
            data.status(mContext.getString(R.string.status_short, model.unread())).visible(true);
            if (model.fromOrMessage() != null) {
                data.expandedTitle(model.fromOrMessage());
                if (mPrefs.getBoolean(mContext.getString(R.string.setting_key_message_subject), false)) {
                    data.expandedBody(model.subject());
                }
            } else {
                data.expandedTitle(mContext.getResources().getQuantityString(R.plurals.title_new_messages, model.unread(), model.unread()));
            }
        } else if (isPersistentNotificationEnabled()) {
            data.status(mContext.getString(R.string.status_short, 0)).visible(true);
            data.expandedTitle(mContext.getString(R.string.title_no_new_messages));
        }
        return data;
    }

    @Override
    public ExtensionData createFrom(Error error) {
        ExtensionData data = new ExtensionData();
        data.status(mContext.getString(R.string.error_status)).icon(R.drawable.ic_gmail).visible(true);

        if (error == Error.NOTIFICATION_ACCESS) {
            data.expandedTitle(mContext.getString(R.string.error_notification_access_title))
                    .expandedBody(mContext.getString(R.string.error_notification_access_body))
                    .clickIntent(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        } else if (error == Error.EXTENSION_ACCESS) {
            data.expandedTitle(mContext.getString(R.string.error_extension_access_title))
                    .expandedBody(mContext.getString(R.string.error_extension_access_body)).clickIntent(new Intent(mContext, SettingsActivity.class));
        }

        return data;
    }

    private boolean isPersistentNotificationEnabled() {
        return mPrefs.getBoolean(mContext.getString(R.string.setting_key_persistent_notification), false);
    }
}