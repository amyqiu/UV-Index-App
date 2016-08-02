package uvindexforecast.theoneandonly.com.uvindexforecast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;

/**
 * Created by Amy on 2016-08-01.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent notificationIntent = new Intent(context, NotificationActivity.class);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(NotificationActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent mPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder mBuilder = new Notification.Builder(context);

        //PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setAutoCancel(false);
        mBuilder.setContentTitle("UV Index Alert");
        mBuilder.setTicker("ticker text here");
        mBuilder.setContentText("Wear sunscreen tomorrow!");
        mBuilder.setSmallIcon(android.R.drawable.ic_menu_day);
        mBuilder.setContentIntent(mPendingIntent);
        //mBuilder.setOngoing(true);
        //API level 16
        mBuilder.setSubText("The predicted UV index for tomorrow is 9");
        mBuilder.setNumber(150);
        mBuilder.build();

        Notification mNotification = mBuilder.build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mNotification);
    }
}