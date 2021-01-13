package controllers;

import controllers.interfaces.ProgressListener;
import controllers.interfaces.SongListener;
import javafx.scene.media.MediaPlayer;
import models.Song;
import utils.Identifier;
import utils.MediaPlayerFx;

import java.io.File;
import java.util.ArrayList;

public class MusicFxController {

    private final ArrayList<MediaPlayerFx> mediaPlayerFxList;
    private MediaPlayerFx currentMediaPlayerFx;
    private double currentVolume;

    public MusicFxController(File[] songFiles, SongListener songListener) {
        // Initialize variables
        mediaPlayerFxList = new ArrayList<>();
        currentMediaPlayerFx = null;
        currentVolume = 0.25;

        // Loop the song files
        for (File songFile : songFiles) {
            // Create a MediaPlayerFx and add it to the array
            MediaPlayerFx mediaPlayerFx = new MediaPlayerFx(songFile);
            mediaPlayerFx.setVolume(currentVolume);
            mediaPlayerFxList.add(mediaPlayerFx);

            // Get the song model
            Song song = mediaPlayerFx.getSong();

            // Send song to listener
            songListener.current(song);
        }
    }

    public double getVolume() {
        return currentVolume;
    }

    public void setVolume(double volume) {
        currentVolume = volume;
        currentMediaPlayerFx.setVolume(volume);
    }

    public boolean hasCurrentMediaPlayerFx() {
        return currentMediaPlayerFx != null;
    }

    public boolean isSongPlaying() {
        return currentMediaPlayerFx.getStatus() == MediaPlayer.Status.PLAYING;
    }

    public void setSongAction(Identifier identifier) {
        if (currentMediaPlayerFx != null) {
            switch (identifier) {
                case PLAY_SONG:
                    currentMediaPlayerFx.play();
                    break;

                case PAUSE_SONG:
                    currentMediaPlayerFx.pause();
                    break;

                case STOP_SONG:
                    currentMediaPlayerFx.stop();
                    break;
            }
        }
    }

    public int getIndexOfCurrentMPFX(int mediaPlayerFxId) {
        int indexOfCurrentMPFX = -1;

        for (MediaPlayerFx mediaPlayerFx : mediaPlayerFxList) {
            if (mediaPlayerFx.getId() == mediaPlayerFxId) {
                indexOfCurrentMPFX = mediaPlayerFxList.indexOf(mediaPlayerFx);
            }
        }

        return indexOfCurrentMPFX;
    }

    // Set the an id to MediaPlayerFx
    public void setMediaPlayerFxId(int id, int index) {
        mediaPlayerFxList.get(index).setId(id);
    }

    // Get the MediaPlayerFx to set current
    public void selectCurrentSong(int mediaPlayerFxId, SongListener songListener) {
        // Stop current MediaPlayingFx
        setSongAction(Identifier.STOP_SONG);

        // Find the MediaPlayerFx to play
        for (MediaPlayerFx mediaPlayerFxItem : mediaPlayerFxList) {
            if (mediaPlayerFxItem.getId() == mediaPlayerFxId) {
                currentMediaPlayerFx = mediaPlayerFxItem;
            }
        }

        // Update volume
        currentMediaPlayerFx.setVolume(currentVolume);

        // Play the current MediaPlayerFx
        setSongAction(Identifier.PLAY_SONG);

        // Send song to listener
        songListener.current(currentMediaPlayerFx.getSong());
    }

    // Listen the buffer progress
    public void setOnCurrentTime(ProgressListener progressListener) {
        if (hasCurrentMediaPlayerFx()) {
            // Get the total duration
            double totalDurationInMillis = currentMediaPlayerFx.getTotalDuration().toMillis();

            // Listen the current property
            currentMediaPlayerFx.currentTimeProperty().addListener((observableValue, oldValue, newValue) -> {
                // Get current duration
                double currentDurationInMillis = newValue.toMillis();

                // Send the total and current duration to listener
                progressListener.changed(totalDurationInMillis, currentDurationInMillis);
            });
        }
    }

    // Set next or previous song
    public int nextOrPreviousMediaPlayerFx(Identifier identifier) {
        int currentMediaPlayerFxId = -1;

        if (hasCurrentMediaPlayerFx()) {
            // First stop the current song
            setSongAction(Identifier.STOP_SONG);

            // Get the next or previous song
            int lastIndexOfMPFX = mediaPlayerFxList.size() - 1;
            int indexOfCurrentMPFX = mediaPlayerFxList.indexOf(currentMediaPlayerFx);

            switch (identifier) {
                case NEXT_SONG:
                    currentMediaPlayerFx = indexOfCurrentMPFX != lastIndexOfMPFX ? mediaPlayerFxList.get(indexOfCurrentMPFX + 1) : mediaPlayerFxList.get(0);
                    currentMediaPlayerFxId = currentMediaPlayerFx.getId();
                    break;

                case PREVIOUS_SONG:
                    currentMediaPlayerFx = indexOfCurrentMPFX != 0 ? mediaPlayerFxList.get(indexOfCurrentMPFX - 1) : mediaPlayerFxList.get(lastIndexOfMPFX);
                    currentMediaPlayerFxId = currentMediaPlayerFx.getId();
                    break;
            }
        }

        return currentMediaPlayerFxId;
    }
}
