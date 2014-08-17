package net.solapps.dashclock.gmail;

import net.solapps.dashclock.notification.service.DashClockExtensionClient;
import net.solapps.dashclock.notification.service.NotificationListenerClient;
import android.app.Application;
import android.preference.PreferenceManager;

public class Gmail extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        NotificationListenerClient.init(new NotificationListenerClient());
        DashClockExtensionClient.init(new DashClockExtensionClient<GmailModel>(NotificationListenerClient.instance(), new GmailModelFactory(),
                new GmailExtensionDataFactory(this)));
    }
}