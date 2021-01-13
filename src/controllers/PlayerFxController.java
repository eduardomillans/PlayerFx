package controllers;

import javafx.application.Platform;
import javafx.scene.Node;
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
import utils.Identifier;
import utils.TimeFormatter;
import utils.UIHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class PlayerFxController {

    // JavaFx controls variables
    public Label directoryLabel;
    public VBox songListVBox;
    public Label titleLabel;
    public Label durationLabel;
    public Label artistLabel;
    public ImageView playPauseImage;
    public ImageView volumeImage;
    public ProgressBar songProgressBar;

    // Variables
    private MusicFxController musicFxController;
    private double xOffset;
    private double yOffset;
    private final String MUSIC_DIRECTORY_PREFIX;
    private final String ICONS_PATH;

    // Initialize variables
    public PlayerFxController() {
        musicFxController = null;
        xOffset = 0.0;
        yOffset = 0.0;
        MUSIC_DIRECTORY_PREFIX = "Music Directory:  ";
        ICONS_PATH = "src/resources/icons/";
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

    // Change the x icon with light color
    public void handleWindowsCloseButtonEntered(MouseEvent mouseEvent) {
        try {
            Image image = new Image(new FileInputStream(ICONS_PATH + "close_light.png"));
            ((ImageView) ((VBox) mouseEvent.getSource()).getChildren().get(0)).setImage(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    // Restore the x icon with the dark color
    public void handleWindowsCloseButtonExited(MouseEvent mouseEvent) {
        try {
            Image image = new Image(new FileInputStream(ICONS_PATH + "close.png"));
            ((ImageView) ((VBox) mouseEvent.getSource()).getChildren().get(0)).setImage(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Get the position in the screen
    public void handleAppPressed(MouseEvent mouseEvent) {
        xOffset = (((GridPane) mouseEvent.getSource()).getScene().getWindow().getX()) - mouseEvent.getScreenX();
        yOffset = (((GridPane) mouseEvent.getSource()).getScene().getWindow().getY()) - mouseEvent.getScreenY();
    }

    // Move the windows in the screen
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
                showSongList(songFiles);
            }
        }
    }

    // Toggle play and pause
    public void handlePlayPauseSong() {
        try {
            if (musicFxController != null && musicFxController.hasCurrentMediaPlayerFx()) {
                String iconFilename;

                if (musicFxController.isSongPlaying()) {
                    musicFxController.setSongAction(Identifier.PAUSE_SONG);
                    iconFilename = "play.png";
                } else {
                    musicFxController.setSongAction(Identifier.PLAY_SONG);
                    iconFilename = "pause.png";
                }

                // Change the icon
                playPauseImage.setImage(new Image(new FileInputStream(ICONS_PATH + iconFilename)));
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void handleNextSong() {
        preNextOrPreviousSongToSelect(musicFxController.nextOrPreviousMediaPlayerFx(Identifier.NEXT_SONG));
    }

    public void handlePreviousSong() {
        preNextOrPreviousSongToSelect(musicFxController.nextOrPreviousMediaPlayerFx(Identifier.PREVIOUS_SONG));
    }

    public void handleChangeVolume(MouseEvent mouseEvent) {
        ((Slider) mouseEvent.getSource()).valueProperty().addListener((observableValue, oldValue, newValue) -> {
            try {
                // Set and get the current volume
                musicFxController.setVolume(newValue.doubleValue() / 100);
                double currentVolume = musicFxController.getVolume();

                // Change the icon
                if (currentVolume >= 0.70) {
                    volumeImage.setImage(new Image(new FileInputStream(ICONS_PATH + "high.png")));
                } else if (currentVolume >= 0.4 && currentVolume < 0.7) {
                    volumeImage.setImage(new Image(new FileInputStream(ICONS_PATH + "medium.png")));
                } else if (currentVolume >= 0.1 && currentVolume < 0.4) {
                    volumeImage.setImage(new Image(new FileInputStream(ICONS_PATH + "low.png")));
                } else {
                    volumeImage.setImage(new Image(new FileInputStream(ICONS_PATH + "muted.png")));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    // ************************************************************************************
    // NO-HANDLER METHODS
    // ************************************************************************************

    private void showSongList(File[] songFiles) {
        // Clear song item view list previously
        songListVBox.getChildren().clear();

        // Create the MusicFxController
        musicFxController = new MusicFxController(songFiles, song -> {
            // Build the view and get their nodes
            HashMap<Identifier, Node> songItemView = UIHelper.buildSongItemView(
                    song.titleProperty(), song.artistProperty(), song.durationProperty()
            );

            // Add the new list into view
            songListVBox.getChildren().add(songItemView.get(Identifier.SONG_ITEM_GRIDPANE));
        });

        // Listen the click on each song item
        AtomicInteger indexSongItemHyperlink = new AtomicInteger(0);
        for (Hyperlink songItemHyperlink : UIHelper.getSongItemHyperlinkList(songListVBox)) {
            // Set the same "id" to hyperlink and MediaPlayerFx
            int mediaPlayerFxId = songItemHyperlink.hashCode();
            musicFxController.setMediaPlayerFxId(mediaPlayerFxId, indexSongItemHyperlink.getAndIncrement());

            songItemHyperlink.setOnMouseClicked(mouseEvent -> {
                // Change hyperlink's text fill
                UIHelper.toggleSongItemColor(songListVBox, songItemHyperlink);

                // Select the song and play it
                selectSongToPlay(mediaPlayerFxId);
            });
        }
    }

    private void selectSongToPlay(int mediaPlayerFxId) {
        try {
            // Variable to reuse
            AtomicReference<String> totalDurationStr = new AtomicReference<>();

            // Set song information
            musicFxController.selectCurrentSong(mediaPlayerFxId, song -> {
                titleLabel.textProperty().bind(song.titleProperty());
                artistLabel.textProperty().bind(song.artistProperty());
                totalDurationStr.set(song.getDuration());
            });

            musicFxController.setOnCurrentTime((totalDurationInMillis, currentDurationInMillis) -> {
                // Set progress in the label
                durationLabel.setText(TimeFormatter.formatSongDuration((long) currentDurationInMillis, totalDurationStr));

                // Set progress value in the progressBar
                double currentProgressValue = ((currentDurationInMillis * 100) / totalDurationInMillis) / 100;
                songProgressBar.setProgress(currentProgressValue);
            });

            // Change the icon
            playPauseImage.setImage(new Image(new FileInputStream(ICONS_PATH + "pause.png")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void preNextOrPreviousSongToSelect(int mediaPlayerFxId) {
        if (mediaPlayerFxId != -1) {
            // Set the song to play
            selectSongToPlay(mediaPlayerFxId);

            // Change the song title color
            ArrayList<Hyperlink> songItemHyperlinkList = UIHelper.getSongItemHyperlinkList(songListVBox);
            for (Hyperlink songItemHyperlink : songItemHyperlinkList) {
                if (songItemHyperlinkList.indexOf(songItemHyperlink) == musicFxController.getIndexOfCurrentMPFX(mediaPlayerFxId)) {
                    UIHelper.toggleSongItemColor(songListVBox, songItemHyperlink);
                }
            }
        }
    }
}
