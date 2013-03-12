package com.schoentoon.ventistipes;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import java.util.Iterator;
import java.util.Random;

public class GCMIntentService extends GCMBaseIntentService {
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

  protected void onError(Context context, String s) {
    broadcast("error", s);
  }

  protected void onRegistered(Context context, String s) {
    broadcast("registered", s);
  }

  protected void onUnregistered(Context context, String s) {
    broadcast("unregistered", s);
  }

  private void broadcast(String type, String s) {
    Intent intent = new Intent(getClass().getName());
    intent.putExtra("type", type);
    intent.putExtra("msg", s);
    sendBroadcast(intent);
    Log.d(type, s);
  }
}
