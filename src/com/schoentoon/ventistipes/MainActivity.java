package com.schoentoon.ventistipes;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        EditText proj_id = (EditText) findViewById(R.id.proj_id);
        GCMRegistrar.register(MainActivity.this, proj_id.getText().toString());
      }
    });
    findViewById(R.id.unregister).setEnabled(GCMRegistrar.isRegistered(this));
    findViewById(R.id.unregister).setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        GCMRegistrar.unregister(MainActivity.this);
      }
    });
    findViewById(R.id.share).setEnabled(GCMRegistrar.isRegistered(this));
    findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, GCMRegistrar.getRegistrationId(MainActivity.this));
        startActivity(intent);
      }
    });
    if (GCMRegistrar.isRegistered(this))
      ((TextView) findViewById(R.id.push_id)).setText(GCMRegistrar.getRegistrationId(this));
    registerReceiver(broadcastReceiver, new IntentFilter(GCMIntentService.class.getName()));
  }

  protected void onDestroy() {
    super.onDestroy();
    GCMRegistrar.onDestroy(this);
    unregisterReceiver(broadcastReceiver);
  }

  private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    public void onReceive(Context context, Intent intent) {
      final String type = intent.getStringExtra("type");
      if ("registered".equals(type)) {
        findViewById(R.id.unregister).setEnabled(true);
        findViewById(R.id.share).setEnabled(true);
        ((TextView) findViewById(R.id.push_id)).setText(intent.getStringExtra("msg"));
      } else if ("unregistered".equals(type)) {
        findViewById(R.id.unregister).setEnabled(false);
        findViewById(R.id.share).setEnabled(false);
        ((TextView) findViewById(R.id.push_id)).setText("");
      } else if ("error".equals(type))
        ((TextView) findViewById(R.id.push_id)).setText("Error: " + intent.getStringExtra("msg"));
    }
  };
}
