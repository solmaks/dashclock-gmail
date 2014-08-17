package net.solapps.dashclock.gmail;

import net.solapps.dashclock.notification.service.IModelFactory.IModel;
import android.app.PendingIntent;

public class GmailModel implements IModel {

    private int mUnread;
    private String mPackageName;
    private String mFromOrMessage;
    private PendingIntent mContentIntent;
    private String mSubject;

    @Override
    public PendingIntent contentIntent() {
        return mContentIntent;
    }

    public GmailModel contentIntent(PendingIntent intent) {
        mContentIntent = intent;
        return this;
    }

    @Override
    public String packageName() {
        return mPackageName;
    }

    public GmailModel packageName(String packageName) {
        mPackageName = packageName;
        return this;
    }

    public int unread() {
        return mUnread;
    }

    public GmailModel unread(int unread) {
        mUnread = unread;
        return this;
    }

    public String fromOrMessage() {
        return mFromOrMessage;
    }

    public GmailModel fromOrMessage(String fromOrMessage) {
        mFromOrMessage = fromOrMessage;
        return this;
    }

    public String subject() {
        return mSubject;
    }

    public GmailModel subject(String subject) {
        mSubject = subject;
        return this;
    }
}