package com.schoentoon.ventistipes;

import android.app.Activity;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import java.io.IOException;

public class MainActivity extends Activity {
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    checkPlayServices();
    gcm = GoogleCloudMessaging.getInstance(this);
    setContentView(R.layout.main);
    findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        EditText proj_id = (EditText) findViewById(R.id.proj_id);
        registerInBackground(MainActivity.this, proj_id.getText().toString());
      }
    });
    findViewById(R.id.unregister).setEnabled(getRegistrationId(this) != null);
    findViewById(R.id.unregister).setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        unregisterInBackground(MainActivity.this);
      }
    });
    findViewById(R.id.share).setEnabled(getRegistrationId(this) != null);
    findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getRegistrationId(MainActivity.this));
        startActivity(intent);
      }
    });
    if (getRegistrationId(this) != null)
      ((TextView) findViewById(R.id.push_id)).setText(getRegistrationId(this));
  }

  private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

  private boolean checkPlayServices() {
    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    if (resultCode != ConnectionResult.SUCCESS) {
      if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
        GooglePlayServicesUtil.getErrorDialog(resultCode, this,
          PLAY_SERVICES_RESOLUTION_REQUEST).show();
      } else {
        Log.i(getClass().getName(), "This device is not supported.");
        finish();
      }
      return false;
    }
    return true;
  }

  private void unregisterInBackground(final Context context) {
    new AsyncTask<Void, Void, String>() {
      protected String doInBackground(Void... voids) {
        try {
          gcm.unregister();
        } catch (IOException e) {
          return "Error: " + e.getMessage();
        }
        return "Successfully unregistered device";
      }

      protected void onPostExecute(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        if (getRegistrationId(MainActivity.this) != null) {
          ((TextView) findViewById(R.id.push_id)).setText(getRegistrationId(MainActivity.this));
          findViewById(R.id.share).setEnabled(getRegistrationId(MainActivity.this) != null);
        } else
          ((TextView) findViewById(R.id.push_id)).setText("");
      }
    }.execute();
  }

  private void registerInBackground(final Context context, String sender_id) {
    new AsyncTask<String, Void, String>() {
      protected String doInBackground(String... params) {
        String msg = "";
        try {
          String regid = gcm.register(params[0]);
          msg = "Device registered, registration ID=" + regid;
          storeRegistrationId(context, regid);
        } catch (IOException ex) {
          msg = "Error :" + ex.getMessage();
        }
        return msg;
      }

      protected void onPostExecute(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        if (getRegistrationId(MainActivity.this) != null) {
          ((TextView) findViewById(R.id.push_id)).setText(getRegistrationId(MainActivity.this));
          findViewById(R.id.share).setEnabled(getRegistrationId(MainActivity.this) != null);
        } else
          ((TextView) findViewById(R.id.push_id)).setText("");
      }
    }.execute(sender_id);
  }

  private static final String PROPERTY_REG_ID = "register_id";

  private void storeRegistrationId(Context context, String regId) {
    final SharedPreferences prefs = getGCMPreferences(context);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString(PROPERTY_REG_ID, regId);
    editor.commit();
  }

  private String getRegistrationId(Context context) {
    final SharedPreferences prefs = getGCMPreferences(context);
    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
    if (registrationId.isEmpty()) {
      Log.i(getClass().getName(), "Registration not found.");
      return null;
    }
    return registrationId;
  }

  private SharedPreferences getGCMPreferences(Context context) {
    return getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
  }

  private GoogleCloudMessaging gcm;
}
