package controllers;

import controllers.interfaces.ProgressListener;
import controllers.interfaces.SongListener;
import javafx.scene.media.MediaPlayer;
import models.Song;
import utils.SongState;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class MusicFxController {

    private final ArrayList<Song> songList;
    private final ArrayList<Song> shufflingSongList;
    private final ArrayList<Song> recentSongList;
    private Song currentSong;
    private MediaPlayer mediaPlayer;
    private boolean isRandomActivate;

    public MusicFxController(File[] songFiles, SongListener songListener) {
        // Initialize variables
        songList = new ArrayList<>();
        shufflingSongList = new ArrayList<>();
        recentSongList = new ArrayList<>();
        currentSong = null;
        mediaPlayer = null;
        isRandomActivate = false;

        // Cycle through song files
        for (File songFile : songFiles) {
            // Create a song model and add it to the array
            Song song = new Song(songFile);
            songList.add(song);

            // Send song to listener
            songListener.current(song);
        }
    }

    // Update volume
    public void setVolume(double volume) {
        if (hasCurrentSong()) {
            mediaPlayer.setVolume(volume);
        }
    }

    // "Flag" to validate random state
    public boolean isRandomActivate() {
        return isRandomActivate;
    }

    public void setRandomActivate(boolean randomActivate) {
        isRandomActivate = randomActivate;
        if (isRandomActivate) {
            shuffle();
        }
    }

    public ArrayList<Song> getRecentSongList() {
        return recentSongList;
    }

    // Sort randomly
    private void shuffle() {
        Random rand = new Random();

        // Reshuffle the list
        if (hasCurrentSong()) {
            // First, clear the shuffle list
            shufflingSongList.clear();
            // After, add the current song into first position of shuffle list
            int indexOfCurrentSong = songList.indexOf(currentSong);
            shufflingSongList.add(songList.get(indexOfCurrentSong));
        }

        // Finish to fill the list
        while (shufflingSongList.size() != songList.size()) {
            int randomIndexToSwap = rand.nextInt(songList.size());
            if (!shufflingSongList.contains(songList.get(randomIndexToSwap))) {
                shufflingSongList.add(songList.get(randomIndexToSwap));
            }
        }
    }

    public boolean hasCurrentSong() {
        return currentSong != null;
    }

    public boolean isSongPlaying() {
        return mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }

    // Return the first song id in the respective list
    public int getFirstSongId() {
        return isRandomActivate ? shufflingSongList.get(0).getId() : songList.get(0).getId();
    }

    // Reuse code
    public void setSongAction(SongState songState) {
        if (hasCurrentSong()) {
            switch (songState) {
                case PLAY_SONG:
                    mediaPlayer.play();
                    break;

                case PAUSE_SONG:
                    mediaPlayer.pause();
                    break;

                case STOP_SONG:
                    mediaPlayer.stop();
                    break;
            }
        }
    }

    public int getIndexOfCurrentSong(int songId) {
        int indexOfCurrentSong = -1;

        for (Song song : songList) {
            if (song.getId() == songId) {
                indexOfCurrentSong = songList.indexOf(song);
            }
        }

        return indexOfCurrentSong;
    }

    private void addSongToRecentList() {
        // Just update the position
        recentSongList.remove(currentSong);
        recentSongList.add(currentSong);
    }

    // Set id to song model
    public void setSongId(int id, int index) {
        songList.get(index).setId(id);
    }

    // Sends the song through the listener
    public void selectCurrentSong(int songId, SongListener songListener) {
        // Stop current song
        setSongAction(SongState.STOP_SONG);

        // Find the song to play
        for (Song songItem : songList) {
            if (songItem.getId() == songId) {
                currentSong = songItem;
            }
        }

        // Initialize the media player
        mediaPlayer = new MediaPlayer(currentSong.getMedia());

        // Play the current song
        setSongAction(SongState.PLAY_SONG);

        // Add current song to recent list
        addSongToRecentList();

        // Send song to listener
        songListener.current(currentSong);
    }

    // Listen the buffer progress
    public void setOnCurrentTime(ProgressListener progressListener) {
        if (hasCurrentSong()) {
            // Get the total duration
            double totalDurationInMillis = currentSong.getMedia().getDuration().toMillis();

            // Listen the current time property
            mediaPlayer.currentTimeProperty().addListener((observableValue, oldValue, newValue) -> {
                // Get current duration
                double currentDurationInMillis = newValue.toMillis();

                // Sends the total and current duration to the listener
                progressListener.changed(totalDurationInMillis, currentDurationInMillis);
            });
        }
    }

    // Listen the end of song
    public void setOnEndMedia(SongListener songListener) {
        if (hasCurrentSong()) {
            mediaPlayer.setOnEndOfMedia(songListener::finished);
        }
    }

    // Change the current song
    public int nextOrPreviousSong(SongState songState) {
        int currentSongId = -1;

        if (hasCurrentSong()) {
            // First stop the current song
            setSongAction(SongState.STOP_SONG);

            // Remove media from media player
            mediaPlayer.dispose();

            // Select the song list to choose
            ArrayList<Song> songListToChoose;

            if (isRandomActivate) {
                songListToChoose = shufflingSongList;
            } else {
                songListToChoose = songList;
            }

            // Get the previous or next song
            int lastIndexOfSongList = songListToChoose.size() - 1;
            int indexOfCurrentSong = songListToChoose.indexOf(currentSong);

            switch (songState) {
                case NEXT_SONG:
                    currentSong = indexOfCurrentSong != lastIndexOfSongList ? songListToChoose.get(indexOfCurrentSong + 1) : songListToChoose.get(0);
                    currentSongId = currentSong.getId();
                    break;

                case PREVIOUS_SONG:
                    currentSong = indexOfCurrentSong != 0 ? songListToChoose.get(indexOfCurrentSong - 1) : songListToChoose.get(lastIndexOfSongList);
                    currentSongId = currentSong.getId();
                    break;
            }
        }

        return currentSongId;
    }

    // Reset media player
    public void dispose() {
        setSongAction(SongState.STOP_SONG);
        mediaPlayer.dispose();
    }
}
