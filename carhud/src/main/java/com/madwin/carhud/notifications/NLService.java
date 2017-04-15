package com.madwin.carhud.notifications;

import android.content.ComponentName;
import android.content.Context;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NLService extends NotificationListenerService {

    private static String TAG = NLService.class.getSimpleName();
    private static ArrayList<CHNotifListener> chNotifListeners =
            new ArrayList<CHNotifListener>();
    private static HashMap<String, String> mediaApps = new HashMap<>();
    private static Context mThis;
    private static MediaSessionManager mediaSessionManager;


    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        mThis = this;
        startMediaSessionManager();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (!isMediaApp(sbn.getPackageName()))
            fireNotification(BaseNotificationHandler.HandleNotification(sbn));

//        Toast.makeText(mThis, "NotificationPosted", Toast.LENGTH_SHORT).show();
        Thread sleepThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updatePlayingMetadata(getMediaControllers());
            }
        });

        sleepThread.run();


    }

    private static void registerMediaSessions() {
        // setup active controllers
        List<MediaController> controllers = getMediaControllers();
        setupMediaControllers(controllers);
        updatePlayingMetadata(controllers);

        mediaSessionManager.addOnActiveSessionsChangedListener(new MediaSessionManager.OnActiveSessionsChangedListener() {
            @Override
            public void onActiveSessionsChanged(@Nullable List<MediaController> controllers) {
//                Toast.makeText(mThis, "MediaControllers Sessions changed", Toast.LENGTH_SHORT).show();
                setupMediaControllers(controllers);
                updatePlayingMetadata(controllers);
            }
        }, new ComponentName(mThis, NLService.class));
    }

    private static void setupMediaControllers(List<MediaController> controllers) {
        for (MediaController mc : controllers) {
            final MediaController fmc = mc;
            Log.i(TAG, mc.getPackageName() + " media controller added");
            mediaApps.put(mc.getPackageName(), mc.getPackageName());
            mc.registerCallback(new MediaController.Callback(){
                @Override
                public void onMetadataChanged(@Nullable MediaMetadata metadata) {
                    super.onMetadataChanged(metadata);
//                    Toast.makeText(mThis, "MetadataChanged", Toast.LENGTH_SHORT).show();
                    updateMetadata(fmc, metadata);
                }

                @Override
                public void onSessionDestroyed() {
                    super.onSessionDestroyed();
//                    Toast.makeText(mThis, "Session Destroyed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPlaybackStateChanged(@NonNull PlaybackState state) {
                    super.onPlaybackStateChanged(state);
//                    Toast.makeText(mThis, "PlaybackStateChanged", Toast.LENGTH_SHORT).show();
                    updateMetadata(fmc, fmc.getMetadata());
                }

                @Override
                public void onSessionEvent(@NonNull String event, @Nullable Bundle extras) {
                    super.onSessionEvent(event, extras);
//                    Toast.makeText(mThis, "SessionEvent", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onExtrasChanged(@Nullable Bundle extras) {
                    super.onExtrasChanged(extras);
//                    Toast.makeText(mThis, "Extras Changed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onQueueChanged(@Nullable List<MediaSession.QueueItem> queue) {
                    super.onQueueChanged(queue);
//                    Toast.makeText(mThis, "Queue Changed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private static List<MediaController> getMediaControllers() {
        return mediaSessionManager.getActiveSessions(new ComponentName(mThis, NLService.class));
    }

    private static void updateMetadata(MediaController mc, MediaMetadata metadata) {
        if (metadata == null) return;
        CHMusic chm = new CHMusic(mc.getPackageName());

        chm.setArtist(metadata.getString(MediaMetadata.METADATA_KEY_ARTIST));
        chm.setTitle(metadata.getString(MediaMetadata.METADATA_KEY_TITLE));
        chm.setAlbum(metadata.getString(MediaMetadata.METADATA_KEY_ALBUM));

        chm.setAlbumArt(metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART));
        fireMusic(chm);
    }

    private static void updatePlayingMetadata(List<MediaController> controllers) {
        for (MediaController controller : controllers) {
            if (controller.getPlaybackState() != null) {
                int playbackState = controller.getPlaybackState().getState();
                if (PlaybackState.STATE_PLAYING == playbackState) {
                    updateMetadata(controller, controller.getMetadata());
                }
            }
        }
    }

    private boolean isMediaApp(String packageName) {
        return mediaApps.containsKey(packageName);
    }

    public static void startMediaSessionManager() {
        if (mThis != null) {
            mediaSessionManager = (MediaSessionManager) mThis.getSystemService(Context.MEDIA_SESSION_SERVICE);
            registerMediaSessions();
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {}

    public static void addNotifListener(CHNotifListener chnl) {
        chNotifListeners.add(chnl);
    }

    public interface CHNotifListener {
        void onNotificationPosted(CHNotification chNotification);
        void onMusicPosted(CHMusic chMusic);
    }

    public static void fireMusic(CHMusic chMusic) {
        for (CHNotifListener chn : chNotifListeners) {
            chn.onMusicPosted(chMusic);
        }
    }
    private void fireNotification(CHNotification chNotification) {
        for (CHNotifListener chn : chNotifListeners) {
            chn.onNotificationPosted(chNotification);
        }
    }

}
