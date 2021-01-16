package controllers;

import controllers.interfaces.SongListener;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import models.Song;
import utils.SongState;
import utils.TimeFormatter;
import utils.UIHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class PlayerFxController {

    // JavaFx controls variables
    public Label directoryLabel;
    public VBox songListVBox;
    public Label titleLabel;
    public Label durationLabel;
    public Label artistLabel;
    public ImageView coverImage;
    public ImageView playPauseImage;
    public ImageView volumeImage;
    public ProgressBar songProgressBar;
    public ImageView randomImage;
    public VBox recentSongListVBox;

    // Variables
    private MusicFxController musicFxController;
    private double currentVolume;
    private boolean hasVolumeListener;
    private double xOffset;
    private double yOffset;
    private final String MUSIC_DIRECTORY_PREFIX;
    private final String ICONS_PATH;
    private final String IMAGES_PATH;

    // Initialize variables
    public PlayerFxController() {
        musicFxController = null;
        currentVolume = 0.25;
        hasVolumeListener = false;
        xOffset = 0.0;
        yOffset = 0.0;
        MUSIC_DIRECTORY_PREFIX = "Music Directory:  ";
        ICONS_PATH = "src/resources/icons/";
        IMAGES_PATH = "src/resources/images/";
    }

    // Minimize the app
    public void handleMinimizeApp(MouseEvent mouseEvent) {
        ((Stage) (((VBox) mouseEvent.getSource()).getScene().getWindow())).setIconified(true);
    }

    // Close the app
    public void handleCloseApp() {
        Platform.exit();
        System.exit(0);
    }

    // Change the x icon with light version
    public void handleWindowsCloseButtonEntered(MouseEvent mouseEvent) {
        try {
            Image image = new Image(new FileInputStream(ICONS_PATH + "close_light.png"));
            ((ImageView) ((VBox) mouseEvent.getSource()).getChildren().get(0)).setImage(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    // Restore the x icon with the dark version
    public void handleWindowsCloseButtonExited(MouseEvent mouseEvent) {
        try {
            Image image = new Image(new FileInputStream(ICONS_PATH + "close.png"));
            ((ImageView) ((VBox) mouseEvent.getSource()).getChildren().get(0)).setImage(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Get position on screen
    public void handleAppPressed(MouseEvent mouseEvent) {
        xOffset = (((GridPane) mouseEvent.getSource()).getScene().getWindow().getX()) - mouseEvent.getScreenX();
        yOffset = (((GridPane) mouseEvent.getSource()).getScene().getWindow().getY()) - mouseEvent.getScreenY();
    }

    // Move the window on the screen
    public void handleAppDragged(MouseEvent mouseEvent) {
        ((GridPane) mouseEvent.getSource()).getScene().getWindow().setX(mouseEvent.getScreenX() + xOffset);
        ((GridPane) mouseEvent.getSource()).getScene().getWindow().setY(mouseEvent.getScreenY() + yOffset);
    }

    // Choose a directory
    public void handleOpenFileChooser(MouseEvent mouseEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select music directory");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Get selected directory
        File selectedDirectory = directoryChooser.showDialog(((StackPane) mouseEvent.getSource()).getScene().getWindow());

        // Set directory path
        if (selectedDirectory != null) {
            directoryLabel.setText(MUSIC_DIRECTORY_PREFIX + selectedDirectory.getAbsolutePath());

            // Get only mp3 files
            File[] songFiles = selectedDirectory.listFiles(file -> file.getName().endsWith(".mp3"));

            // Show the song list view
            if (songFiles != null && songFiles.length != 0) {
                // Reset old media player before adding music directory
                if (musicFxController != null) {
                    resetPlayerFx();
                }
                showSongList(songFiles);
            }
        }
    }

    // Toggle play and pause
    public void handlePlayOrPauseSong() {
        try {
            if (musicFxController != null && musicFxController.hasCurrentSong()) {
                String iconFilename;

                if (musicFxController.isSongPlaying()) {
                    musicFxController.setSongAction(SongState.PAUSE_SONG);
                    iconFilename = "play.png";
                } else {
                    musicFxController.setSongAction(SongState.PLAY_SONG);
                    iconFilename = "pause.png";
                }

                // Change the icon
                playPauseImage.setImage(new Image(new FileInputStream(ICONS_PATH + iconFilename)));
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    // Select the next song
    public void handleNextSong() {
        if (musicFxController != null) {
            preNextOrPreviousSongToSelect(musicFxController.nextOrPreviousSong(SongState.NEXT_SONG));
        }
    }

    // Select the previous song
    public void handlePreviousSong() {
        if (musicFxController != null) {
            preNextOrPreviousSongToSelect(musicFxController.nextOrPreviousSong(SongState.PREVIOUS_SONG));
        }
    }

    // Control the volume
    public void handleChangeVolume(MouseEvent mouseEvent) {
        Slider volumeSlider = ((Slider) mouseEvent.getSource());

        if (!hasVolumeListener) {
            currentVolume = volumeSlider.getValue() / 100;
            changeVolumeIcon();
            hasVolumeListener = true;
        }

        volumeSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            currentVolume = newValue.doubleValue() / 100;

            if (musicFxController != null) {
                musicFxController.setVolume(currentVolume);
            }

            changeVolumeIcon();
        });
    }

    // Set the playlist random or non-random
    public void handleToggleRandom() {
        if (musicFxController != null) {
            try {
                boolean randomValue = !musicFxController.isRandomActivate();
                musicFxController.setRandomActivate(randomValue);

                if (randomValue) {
                    randomImage.setImage(new Image(new FileInputStream(ICONS_PATH + "no_random.png")));
                } else {
                    randomImage.setImage(new Image(new FileInputStream(ICONS_PATH + "random.png")));
                }
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    // The only difference with "handlePlayOrPauseSong" is that with this method only the song is played
    public void handlePlaySong() {
        try {
            if (musicFxController != null) {
                if (musicFxController.hasCurrentSong()) {
                    if (!musicFxController.isSongPlaying()) {
                        musicFxController.setSongAction(SongState.PLAY_SONG);
                        playPauseImage.setImage(new Image(new FileInputStream(ICONS_PATH + "pause.png")));
                    }
                } else {
                    selectSongToPlay(musicFxController.getFirstSongId());

                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    // NO-HANDLER METHODS

    // Create dynamic song list from user interface
    private void showSongList(File[] songFiles) {
        // Reset the old song list
        songListVBox.getChildren().clear();

        // Create the MusicFxController
        musicFxController = new MusicFxController(songFiles, new SongListener() {
            @Override
            public void current(Song song) {
                // Build the view and get its nodes
                GridPane songItemView = UIHelper.buildSongItemView(
                        song.titleProperty(), song.artistProperty(), song.durationProperty()
                );

                // Add the new list to view
                songListVBox.getChildren().add(songItemView);
            }

            @Override
            public void finished() {

            }
        });

        // Listen the click of each item in the song
        AtomicInteger indexSongItemHyperlink = new AtomicInteger(0);
        for (Hyperlink songItemHyperlink : UIHelper.getSongItemHyperlinkList(songListVBox)) {
            // Set the same "id" for hyperlink and MediaPlayerFx
            int songId = songItemHyperlink.hashCode();
            musicFxController.setSongId(songId, indexSongItemHyperlink.getAndIncrement());

            songItemHyperlink.setOnMouseClicked(mouseEvent -> {
                // Select the song and play it
                selectSongToPlay(songId);
            });
        }
    }

    // With the music controller select the song to play
    private void selectSongToPlay(int songId) {
        try {
            // Variable to reuse
            AtomicReference<String> totalDurationStr = new AtomicReference<>();

            // Set song information
            musicFxController.selectCurrentSong(songId, new SongListener() {
                @Override
                public void current(Song song) {
                    titleLabel.textProperty().bind(song.titleProperty());
                    artistLabel.textProperty().bind(song.artistProperty());
                    totalDurationStr.set(song.getDuration());
                    coverImage.setImage(song.getCover());
                }

                @Override
                public void finished() {

                }
            });

            // Update volume
            musicFxController.setVolume(currentVolume);

            // Update the song's hyperlink to the recent list
            updateRecentSongList();

            // Change title color
            ArrayList<Hyperlink> songItemHyperlinkList = UIHelper.getSongItemHyperlinkList(songListVBox);
            for (Hyperlink songItemHyperlink : songItemHyperlinkList) {
                if (songItemHyperlinkList.indexOf(songItemHyperlink) == musicFxController.getIndexOfCurrentSong(songId)) {
                    UIHelper.toggleSongTitleColor(songListVBox, songItemHyperlink);
                }
            }

            // Listen the setOnCurrentTime
            musicFxController.setOnCurrentTime((totalDurationInMillis, currentDurationInMillis) -> {
                // Set progress in the label
                durationLabel.setText(TimeFormatter.formatSongDuration((long) currentDurationInMillis, totalDurationStr));

                // Set progress value in the progressBar
                double currentProgressValue = ((currentDurationInMillis * 100) / totalDurationInMillis) / 100;
                songProgressBar.setProgress(currentProgressValue);
            });

            // Listen the end of the song
            musicFxController.setOnEndMedia(new SongListener() {
                @Override
                public void current(Song song) {

                }

                @Override
                public void finished() {
                    preNextOrPreviousSongToSelect(musicFxController.nextOrPreviousSong(SongState.NEXT_SONG));
                }
            });

            // Change the icon
            playPauseImage.setImage(new Image(new FileInputStream(ICONS_PATH + "pause.png")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Reuse the code in the "next" or "previous" actions
    private void preNextOrPreviousSongToSelect(int songId) {
        if (songId != -1) {
            selectSongToPlay(songId);
        }
    }

    // When selecting a different music directory
    private void resetPlayerFx() {
        try {
            // Release the media player
            musicFxController.dispose();

            // Reset the song information
            titleLabel.textProperty().unbind();
            titleLabel.setText("");
            artistLabel.textProperty().unbind();
            artistLabel.setText("");
            coverImage.setImage(new Image(new FileInputStream(IMAGES_PATH + "placeholder.png")));

            // Change the icon
            playPauseImage.setImage(new Image(new FileInputStream(ICONS_PATH + "play.png")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Refresh recent song list in view
    private void updateRecentSongList() {
        ArrayList<Song> recentSongList = musicFxController.getRecentSongList();

        recentSongListVBox.getChildren().clear();

        for (int i = (recentSongList.size() - 1); i >= 0; i--) {
            Hyperlink songHyperlink = new Hyperlink(recentSongList.get(i).getTitle());
            songHyperlink.getStyleClass().add("link");
            VBox.setMargin(songHyperlink, new Insets(0, 0, 10, 0));

            int indexSong = i;
            songHyperlink.setOnMouseClicked(mouseEvent -> selectSongToPlay(recentSongList.get(indexSong).getId()));

            recentSongListVBox.getChildren().add(songHyperlink);
        }
    }

    // Change the icon
    private void changeVolumeIcon() {
        try {
            if (currentVolume >= 0.70) {
                volumeImage.setImage(new Image(new FileInputStream(ICONS_PATH + "high.png")));
            } else if (currentVolume >= 0.4 && currentVolume < 0.7) {
                volumeImage.setImage(new Image(new FileInputStream(ICONS_PATH + "medium.png")));
            } else if (currentVolume >= 0.1 && currentVolume < 0.4) {
                volumeImage.setImage(new Image(new FileInputStream(ICONS_PATH + "low.png")));
            } else {
                volumeImage.setImage(new Image(new FileInputStream(ICONS_PATH + "muted.png")));
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}