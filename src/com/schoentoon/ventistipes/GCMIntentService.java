package com.schoentoon.ventistipes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import java.util.Iterator;

public class GCMIntentService extends GCMBaseIntentService {
  protected void onMessage(Context context, Intent intent) {
    final Bundle bundle = intent.getExtras();
    for (Iterator<String> iter = bundle.keySet().iterator();iter.hasNext();) {
      String key = iter.next();
      Log.d(bundle.getClass().getSimpleName(), "Key: " + key + ", data: " + bundle.get(key).toString());
    }
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
