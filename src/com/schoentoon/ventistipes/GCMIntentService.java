package com.schoentoon.ventistipes;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Iterator;
import java.util.Random;

public class GCMIntentService extends IntentService {
  public GCMIntentService() {
    super(GCMIntentService.class.getName());
  }

  protected void onMessage(Context context, Intent intent) {
    final Bundle bundle = intent.getExtras();
    for (Iterator<String> iter = bundle.keySet().iterator();iter.hasNext();) {
      String key = iter.next();
      Log.d(bundle.getClass().getSimpleName(), "Key: " + key + ", data: " + bundle.get(key).toString());
    }
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
    builder.setContentTitle(bundle.getString("subject","No subject"));
    builder.setContentText(bundle.getString("data", "No data"));
    builder.setSubText(bundle.getString("sender", "No sender"));
    builder.setStyle(new NotificationCompat.BigTextStyle(builder)
            .bigText(bundle.getString("data", "No data"))
            .setBigContentTitle(bundle.getString("subject", "No subject"))
            .setSummaryText(bundle.getString("sender", "No sender")));
    builder.setSmallIcon(android.R.drawable.stat_notify_error);
    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    nm.notify(new Random().nextInt(), builder.build());
  }

  protected void onHandleIntent(Intent intent) {
    Bundle extras = intent.getExtras();
    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
    String messageType = gcm.getMessageType(intent);
    if (!extras.isEmpty() && GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
      for (Iterator<String> iter = extras.keySet().iterator();iter.hasNext();) {
        String key = iter.next();
        Log.d(extras.getClass().getSimpleName(), "Key: " + key + ", data: " + extras.get(key).toString());
      }
      NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
      builder.setContentTitle(extras.getString("subject","No subject"));
      builder.setContentText(extras.getString("data", "No data"));
      builder.setSubText(extras.getString("sender", "No sender"));
      builder.setStyle(new NotificationCompat.BigTextStyle(builder)
        .bigText(extras.getString("data", "No data"))
        .setBigContentTitle(extras.getString("subject", "No subject"))
        .setSummaryText(extras.getString("sender", "No sender")));
      builder.setSmallIcon(android.R.drawable.stat_notify_error);
      NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
      nm.notify(new Random().nextInt(), builder.build());
    }
    GCMBroadcastReceiver.completeWakefulIntent(intent);
  }
}
