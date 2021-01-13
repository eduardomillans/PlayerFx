package utils;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import models.Song;

import java.io.File;

public class MediaPlayerFx {

    private int id;

    private final Song song;
    private final MediaPlayer mediaPlayer;

    public MediaPlayerFx(File songFile) {
        id = 0;

        mediaPlayer = new MediaPlayer(new Media(songFile.toURI().toString()));
        song = new Song();

        mediaPlayer.setOnReady(() -> {
            Object titleMeta = mediaPlayer.getMedia().getMetadata().get("title");
            song.titleProperty().set(titleMeta != null ? titleMeta.toString() : songFile.getName());
            Object artistMeta = mediaPlayer.getMedia().getMetadata().get("artist");
            song.artistProperty().set(artistMeta != null ? artistMeta.toString() : "unknown");
            Object albumMeta = mediaPlayer.getMedia().getMetadata().get("album");
            song.albumProperty().set(albumMeta != null ? albumMeta.toString() : "");
            Object yearMeta = mediaPlayer.getMedia().getMetadata().get("year");
            song.yearProperty().set(yearMeta != null ? yearMeta.toString() : "");
            Object coverMeta = mediaPlayer.getMedia().getMetadata().get("cover");
            song.coverProperty().set(coverMeta != null ? coverMeta.toString() : "");
            song.durationProperty().set(TimeFormatter.convertMillisToStrTime((long) mediaPlayer.getMedia().getDuration().toMillis()));
        });
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Song getSong() {
        return song;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void play() {
        mediaPlayer.play();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void stop() {
        mediaPlayer.stop();
    }

    public MediaPlayer.Status getStatus() {
        return mediaPlayer.getStatus();
    }

    public void setVolume(Double volume) {
        mediaPlayer.setVolume(volume);
    }

    public ReadOnlyObjectProperty<Duration> currentTimeProperty() {
        return mediaPlayer.currentTimeProperty();
    }

    public Duration getTotalDuration() {
        return mediaPlayer.getTotalDuration();
    }

    public void setStartTime(Duration duration) {
        mediaPlayer.setStartTime(duration);
    }
}
