package edu.oakland.festinfo.activities;

import java.util.List;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
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
            String currentStage = "";
            ParseInstallation.getCurrentInstallation().saveInBackground();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Artist");
            final long currentTime = System.currentTimeMillis()/1000;
            switch (transition) {
                case Geofence.GEOFENCE_TRANSITION_DWELL:
                    Log.v(TAG, "Geofence Dwelling");
                    for (int i = 0; i < geofencingEvent.getTriggeringGeofences().size(); i++) {
                        if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("RanchArea")) {
                            query.whereEqualTo("location", "ranch area");
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    String message = "";
                                    String currentBand = "";
                                    for (int i = 0; i < objects.size(); i++) {
                                        long startTime = objects.get(i).getLong("startTimeDec3");
                                        long endTime = objects.get(i).getLong("endTimeDec3");
                                        if (startTime <= currentTime && currentTime <= endTime) {
                                            currentBand = objects.get(i).getString("name");
                                            message = currentBand + " is currently playing.";
                                            break;
                                        }
                                    }
                                    if (message.equals("")) {
                                        sendNotification(GeofenceIntentService.this, "No band is currently playing.", "Ranch Area");
                                    } else {
                                        sendNotification(GeofenceIntentService.this, message, "Ranch Area");
                                    }
                                }
                            });
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("SherwoodCourt")) {
                            query.whereEqualTo("location", "sherwood court");
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    String message = "";
                                    String currentBand = "";
                                    for (int i = 0; i < objects.size(); i++) {
                                        long startTime = objects.get(i).getLong("startTimeDec3");
                                        long endTime = objects.get(i).getLong("endTimeDec3");
                                        if (startTime <= currentTime && currentTime <= endTime) {
                                            currentBand = objects.get(i).getString("name");
                                            message = currentBand + " is currently playing.";
                                            break;
                                        }
                                    }
                                    if (message.equals("")) {
                                        sendNotification(GeofenceIntentService.this, "No band is currently playing.", "Sherwood Court");
                                    } else {
                                        sendNotification(GeofenceIntentService.this, message, "Sherwood Court");
                                    }
                                }
                            });
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("Tripolee")) {
                            query.whereEqualTo("location", "tripolee");
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    String message = "";
                                    String currentBand = "";
                                    for (int i = 0; i < objects.size(); i++) {
                                        long startTime = objects.get(i).getLong("startTimeDec3");
                                        long endTime = objects.get(i).getLong("endTimeDec3");
                                        if (startTime <= currentTime && currentTime <= endTime) {
                                            currentBand = objects.get(i).getString("name");
                                            message = currentBand + " is currently playing.";
                                            break;
                                        }
                                    }
                                    if (message.equals("")) {
                                        sendNotification(GeofenceIntentService.this, "No band is currently playing.", "Tripolee");
                                    } else {
                                        sendNotification(GeofenceIntentService.this, message, "Tripolee");
                                    }
                                }
                            });
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("TheHangar")) {
                            query.whereEqualTo("location", "the hangar");
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    String message = "";
                                    String currentBand = "";
                                    for (int i = 0; i < objects.size(); i++) {
                                        long startTime = objects.get(i).getLong("startTimeDec3");
                                        long endTime = objects.get(i).getLong("endTimeDec3");
                                        if (startTime <= currentTime && currentTime <= endTime) {
                                            currentBand = objects.get(i).getString("name");
                                            message = currentBand + " is currently playing.";
                                            break;
                                        }
                                    }
                                    if (message.equals("")) {
                                        sendNotification(GeofenceIntentService.this, "No band is currently playing.", "The Hanagar");
                                    } else {
                                        sendNotification(GeofenceIntentService.this, message, "The Hangar");
                                    }
                                }
                            });
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("Jubilee")) {
                            query.whereEqualTo("location", "jubilee");
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    String message = "";
                                    String currentBand = "";
                                    for (int i = 0; i < objects.size(); i++) {
                                        long startTime = objects.get(i).getLong("startTimeDec3");
                                        long endTime = objects.get(i).getLong("endTimeDec3");
                                        if (startTime <= currentTime && currentTime <= endTime) {
                                            currentBand = objects.get(i).getString("name");
                                            message = currentBand + " is currently playing.";
                                            break;
                                        }
                                    }
                                    if (message.equals("")) {
                                        sendNotification(GeofenceIntentService.this, "No band is currently playing.", "Jubilee");
                                    } else {
                                        sendNotification(GeofenceIntentService.this, message, "Jubilee");
                                    }
                                }
                            });
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("ForestStage")) {
                            query.whereEqualTo("location", "forest stage");
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    String message = "";
                                    String currentBand = "";
                                    for (int i = 0; i < objects.size(); i++) {
                                        long startTime = objects.get(i).getLong("startTimeDec3");
                                        long endTime = objects.get(i).getLong("endTimeDec3");
                                        if (startTime <= currentTime && currentTime <= endTime) {
                                            currentBand = objects.get(i).getString("name");
                                            message = currentBand + " is currently playing.";
                                            break;
                                        }
                                    }
                                    if (message.equals("")) {
                                        sendNotification(GeofenceIntentService.this, "No band is currently playing.", "Forest Stage");
                                    } else {
                                        sendNotification(GeofenceIntentService.this, message, "Forest Stage");
                                    }
                                }
                            });
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("TheObservatory")) {
                            query.whereEqualTo("location", "the observatory");
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    String message = "";
                                    String currentBand = "";
                                    for (int i = 0; i < objects.size(); i++) {
                                        long startTime = objects.get(i).getLong("startTimeDec3");
                                        long endTime = objects.get(i).getLong("endTimeDec3");
                                        if (startTime <= currentTime && currentTime <= endTime) {
                                            currentBand = objects.get(i).getString("name");
                                            message = currentBand + " is currently playing.";
                                            break;
                                        }
                                    }
                                    if (message.equals("")) {
                                        sendNotification(GeofenceIntentService.this, "No band is currently playing.", "The Observatory");
                                    } else {
                                        sendNotification(GeofenceIntentService.this, message, "The Observatory");
                                    }
                                }
                            });
                        }
                    }
                    break;
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    Log.v(TAG, "Geofence Exited");
                    for (int i = 0; i < geofencingEvent.getTriggeringGeofences().size(); i++) {
                        if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("RanchArea")) {
                            sendNotification(this, "Leaving Ranch Area", "Ranch Area");
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("SherwoodCourt")) {
                            sendNotification(this, "Leaving Sherwood Court", "Sherwood Court");
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("Tripolee")) {
                            sendNotification(this, "Leaving Tripolee", "Tripolee");
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("TheHangar")) {
                            sendNotification(this, "Leaving The Hangar", "The Hanagar");
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("Jubilee")) {
                            sendNotification(this, "Leaving Jubilee", "Jubilee");
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("ForestStage")) {
                            sendNotification(this, "Leaving Forest Stage", "Forest Stage");
                        } else if (geofencingEvent.getTriggeringGeofences().get(i).getRequestId().equals("TheObservatory")) {
                            sendNotification(this, "Leaving The Obseratory", "The Observatory");
                        }
                    }
                    break;
                default:
                    sendNotification(this, "Not nearby any stage", "Amplify!");
            }
        }
    }

    private void sendNotification(Context context, String notificationText, String notificationTitle) {
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
