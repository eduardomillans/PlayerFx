package models;

import javafx.beans.property.SimpleStringProperty;

public class Song {

    private final SimpleStringProperty title;
    private final SimpleStringProperty artist;
    private final SimpleStringProperty album;
    private final SimpleStringProperty year;
    private final SimpleStringProperty cover;
    private final SimpleStringProperty duration;

    public Song() {
        title = new SimpleStringProperty(this, "title");
        artist = new SimpleStringProperty(this, "artist");
        album = new SimpleStringProperty(this, "album");
        year = new SimpleStringProperty(this, "year");
        cover = new SimpleStringProperty(this, "cover");
        duration = new SimpleStringProperty(this, "duration");
    }

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getArtist() {
        return artist.get();
    }

    public SimpleStringProperty artistProperty() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist.set(artist);
    }

    public String getAlbum() {
        return album.get();
    }

    public SimpleStringProperty albumProperty() {
        return album;
    }

    public void setAlbum(String album) {
        this.album.set(album);
    }

    public String getYear() {
        return year.get();
    }

    public SimpleStringProperty yearProperty() {
        return year;
    }

    public void setYear(String year) {
        this.year.set(year);
    }

    public String getCover() {
        return cover.get();
    }

    public SimpleStringProperty coverProperty() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover.set(cover);
    }

    public String getDuration() {
        return duration.get();
    }

    public SimpleStringProperty durationProperty() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration.set(duration);
    }
}
