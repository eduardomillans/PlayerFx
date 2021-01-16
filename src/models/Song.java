package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import utils.TimeFormatter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Song {

    private int id;
    private final SimpleStringProperty title;
    private final SimpleStringProperty artist;
    private final SimpleStringProperty album;
    private final SimpleStringProperty year;
    private final SimpleStringProperty duration;
    private Image cover;
    private Media media;

    public Song(File songFile) {
        // Initialize variables
        id = 0;
        title = new SimpleStringProperty(this, "title");
        artist = new SimpleStringProperty(this, "artist");
        album = new SimpleStringProperty(this, "album");
        year = new SimpleStringProperty(this, "year");
        duration = new SimpleStringProperty(this, "duration");

        // Bind the metadata
        media = new Media(songFile.toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        // Get the metadata and populate the variable content
        mediaPlayer.setOnReady(() -> {
            try {
                Object titleMeta = media.getMetadata().get("title");
                String alternativeSongTitle = songFile.getName().substring(0, (songFile.getName().length() - 5));
                title.set(titleMeta != null ? titleMeta.toString() : alternativeSongTitle);
                Object artistMeta = media.getMetadata().get("artist");
                artist.set(artistMeta != null ? artistMeta.toString() : "unknown");
                Object albumMeta = media.getMetadata().get("album");
                album.set(albumMeta != null ? albumMeta.toString() : "");
                Object yearMeta = media.getMetadata().get("year");
                year.set(yearMeta != null ? yearMeta.toString() : "");
                Object coverMeta = media.getMetadata().get("image");
                cover = coverMeta != null ? (Image) coverMeta : new Image(new FileInputStream("src/resources/images/placeholder.png"));
                duration.set(TimeFormatter.convertMillisToStrTime((long) media.getDuration().toMillis()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Media getMedia() {
        return media;
    }

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public SimpleStringProperty artistProperty() {
        return artist;
    }

    public Image getCover() {
        return cover;
    }

    public String getDuration() {
        return duration.get();
    }

    public SimpleStringProperty durationProperty() {
        return duration;
    }
}
