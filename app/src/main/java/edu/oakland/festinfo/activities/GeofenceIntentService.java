package edu.oakland.festinfo.activities;

import java.util.List;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;

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
import edu.oakland.festinfo.activities.MapPageActivity;

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
            String currentStage = "";
            ParseInstallation.getCurrentInstallation().saveInBackground();
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            ParsePush push = new ParsePush();
            Log.d(TAG, installation.getInstallationId());

            switch (transition) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    notificationTitle = "Geofence Entered";
                    Log.v(TAG, "Geofence Entered");
                    for (int i = 0; i < geofencingEvent.getTriggeringGeofences().size(); i++) {
                        if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("RanchArea")) {
                            push.subscribeInBackground("RanchArea");
                            installation.saveInBackground();
                            Log.d(TAG, "Ranch Area Geofence found");
                            currentStage = "Ranch Area";
                            //MapPageActivity.addLocationToPage("Ranch Area");
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("SherwoodCourt")) {
                            push.subscribeInBackground("SherwoodCourt");
                            installation.saveInBackground();
                            Log.d(TAG, "Sherwood Court Geofence found");
                            currentStage = "Sherwood Court";
                            //MapPageActivity.addLocationToPage("Sherwood Court");
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("Tripolee")) {
                            push.subscribeInBackground("Tripolee");
                            installation.saveInBackground();
                            Log.d(TAG, "Tripolee Geofence found");
                            currentStage = "Tripolee";
                            //MapPageActivity.addLocationToPage("Tripolee");
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("TheHangar")) {
                            push.subscribeInBackground("TheHangar");
                            installation.saveInBackground();
                            Log.d(TAG, "The Hangar Geofence found");
                            currentStage = "The Hangar";
                            //MapPageActivity.addLocationToPage("The Hangar");
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("Jubilee")) {
                            push.subscribeInBackground("Jubilee");
                            installation.saveInBackground();
                            Log.d(TAG, "Jubilee Geofence found");
                            currentStage = "Jubilee";
                            //MapPageActivity.addLocationToPage("Jubilee");
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("ForestStage")) {
                            push.subscribeInBackground("ForestStage");
                            installation.saveInBackground();
                            Log.d(TAG, "Forest Stage Geofence found");
                            currentStage = "Forest Stage";
                            //MapPageActivity.addLocationToPage("Forest Stage");
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("TheObservatory")) {
                            push.subscribeInBackground("TheObservatory");
                            installation.saveInBackground();
                            Log.d(TAG, "The Observatory Geofence found");
                            currentStage = "The Observatory";
                            //MapPageActivity.addLocationToPage("The Observatory");
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
                        if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("RanchArea")) {
                            push.unsubscribeInBackground("RanchArea");
                            Log.d(TAG, "Ranch Area Geofence removed");
                            installation.saveInBackground();
                            currentStage = "Ranch Area";
                            //MapPageActivity.removeLocationFromPage(" ");
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("SherwoodCourt")) {
                            push.unsubscribeInBackground("SherwoodCourt");
                            Log.d(TAG, "Sherwood Court Geofence removed");
                            installation.saveInBackground();
                            currentStage = "Sherwood Court";
                            //MapPageActivity.removeLocationFromPage(" ");
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("Tripolee")) {
                            push.unsubscribeInBackground("Tripolee");
                            Log.d(TAG, "Tripolee Geofence removed");
                            installation.saveInBackground();
                            currentStage = "Tripolee";
                            //MapPageActivity.removeLocationFromPage(" ");
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("TheHangar")) {
                            push.unsubscribeInBackground("TheHangar");
                            Log.d(TAG, "The Hangar Geofence removed");
                            installation.saveInBackground();
                            currentStage = "The Hangar";
                            //MapPageActivity.removeLocationFromPage(" ");
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("Jubilee")) {
                            push.unsubscribeInBackground("Jubilee");
                            Log.d(TAG, "Jubilee Geofence removed");
                            installation.saveInBackground();
                            currentStage = "Jubilee";
                            //MapPageActivity.removeLocationFromPage(" ");
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("ForestStage")) {
                            push.unsubscribeInBackground("ForestStage");
                            Log.d(TAG, "Forest Stage Geofence removed");
                            installation.saveInBackground();
                            currentStage = "Forest Stage";
                            //MapPageActivity.removeLocationFromPage(" ");
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("TheObservatory")) {
                            push.unsubscribeInBackground("TheObservatory");
                            Log.d(TAG, "The Observatory Geofence removed");
                            installation.saveInBackground();
                            currentStage = "The Observatory";
                            //MapPageActivity.removeLocationFromPage(" ");
                        } else {
                            Log.d(TAG, "No Geofence removed");
                        }
                    }
                    break;
                default:
                    notificationTitle = "Geofence Unknown";
            }

            sendNotifiation(this, currentStage, notificationTitle);
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
