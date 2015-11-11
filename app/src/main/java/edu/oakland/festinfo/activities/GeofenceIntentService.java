package edu.oakland.festinfo.activities;

import java.util.List;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import bolts.Continuation;
import bolts.Task;
import edu.oakland.festinfo.R;

/**
 * Created by Devin on 11/9/2015.
 */
public class GeofenceIntentService extends IntentService{

    private final String TAG = this.getClass().getCanonicalName();

    public GeofenceIntentService() {
        super("GeofenceIntentService");
        Log.v(TAG, "Constuctor");
    }

    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "OnCreate");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        Log.v(TAG, "OnHandleEvent");
        if (!geofencingEvent.hasError()) {
            int transition = geofencingEvent.getGeofenceTransition();
            String notificationTitle;
            ParseInstallation.getCurrentInstallation().saveInBackground();
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            Log.d(TAG, installation.getInstallationId());

            switch (transition) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    notificationTitle = "Geofence Entered";
                    Log.v(TAG, "Geofence Entered");
                    for (int i = 0; i < geofencingEvent.getTriggeringGeofences().size(); i++) {
                        if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("EC")) {
                            installation.put("EC", true);
                            Log.d(TAG, "EC Geofence found");
                            installation.saveInBackground();
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("MSC")) {
                            installation.put("MSC", true);
                            Log.d(TAG, "MSC Geofence found");
                            installation.saveInBackground();
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("Rec")) {
                            installation.put("Rec", true);
                            Log.d(TAG, "Rec Geofence found");
                            installation.saveInBackground();
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("OC")) {
                            installation.put("OC", true);
                            Log.d(TAG, "OC Geofence found");
                            installation.saveInBackground();
                        } else {
                            Log.d(TAG, "No Geofence Found");
                        }
                    }
                    break;
                case Geofence.GEOFENCE_TRANSITION_DWELL:
                    notificationTitle = "Geofence Dwell";
                    Log.v(TAG, "Geofence Dwell");
                    break;
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    notificationTitle = "Geofence Exit";
                    Log.v(TAG, "Geofence Exited");
                    for (int i = 0; i < geofencingEvent.getTriggeringGeofences().size(); i++) {
                        if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("EC")) {
                            installation.put("EC", false);
                            Log.d(TAG, "EC Geofence removed");
                            installation.saveInBackground();
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("MSC")) {
                            installation.put("MSC", false);
                            Log.d(TAG, "MSC Geofence removed");
                            installation.saveInBackground();
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("Rec")) {
                            installation.put("Rec", false);
                            Log.d(TAG, "Rec Geofence removed");
                            installation.saveInBackground();
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("OC")) {
                            installation.put("OC", false);
                            Log.d(TAG, "OC Geofence removed");
                            installation.saveInBackground();
                        } else {
                            Log.d(TAG, "No Geofence removed");
                        }
                    }
                    break;
                default:
                    notificationTitle = "Geofence Unknown";
            }

            sendNotifiation(this, getTriggeringGeofences(intent), notificationTitle);
        }
    }

    private void sendNotifiation(Context context, String notificationText, String notificationTitle) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wakeLock.acquire();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setDefaults(Notification.DEFAULT_ALL).setAutoCancel(false);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

        wakeLock.release();
    }

    private String getTriggeringGeofences(Intent intent) {
        GeofencingEvent geofenceEvent = GeofencingEvent.fromIntent(intent);
        List<Geofence> geofences = geofenceEvent.getTriggeringGeofences();
        String[] geofenceIds = new String[geofences.size()];

        for (int i = 0; i < geofences.size(); i++) {
            geofenceIds[i] = geofences.get(i).getRequestId();
            Log.d(TAG, geofences.get(i).getRequestId());
        }

        return TextUtils.join(", ", geofenceIds);
    }
}
