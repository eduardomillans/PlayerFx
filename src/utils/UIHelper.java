package utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;

public class UIHelper {

    public static HashMap<Identifier, Node> buildSongItemView(
            SimpleStringProperty titleProperty,
            SimpleStringProperty artistProperty,
            SimpleStringProperty durationProperty
    ) {
        Hyperlink titleHyperlink = new Hyperlink();
        titleHyperlink.textProperty().bind(titleProperty);
        titleHyperlink.getStyleClass().add("song");
        Label artistLabel = new Label();
        artistLabel.textProperty().bind(artistProperty);
        artistLabel.getStyleClass().add("artist");
        Label durationLabel = new Label();
        durationLabel.textProperty().bind(durationProperty);
        durationLabel.getStyleClass().add("time");

        VBox vBox = new VBox(titleHyperlink, artistLabel);
        vBox.getStyleClass().add("item");
        VBox.setMargin(titleHyperlink, new Insets(0, 0, 5, 0));

        GridPane gridPane = new GridPane();
        VBox.setMargin(gridPane, new Insets(0, 0, 12, 0));

        ColumnConstraints firstColumnConstrains = new ColumnConstraints();
        firstColumnConstrains.setHgrow(Priority.SOMETIMES);
        firstColumnConstrains.setHalignment(HPos.LEFT);
        ColumnConstraints secondColumnConstrains = new ColumnConstraints();
        secondColumnConstrains.setPercentWidth(20);
        secondColumnConstrains.setHgrow(Priority.SOMETIMES);
        secondColumnConstrains.setHalignment(HPos.RIGHT);

        gridPane.addColumn(0, vBox);
        gridPane.addColumn(1, durationLabel);
        gridPane.getColumnConstraints().addAll(firstColumnConstrains, secondColumnConstrains);

        HashMap<Identifier, Node> hashMap = new HashMap<>();
        hashMap.put(Identifier.SONG_ITEM_GRIDPANE, gridPane);
        hashMap.put(Identifier.SONG_ITEM_HYPERLINK, titleHyperlink);

        return hashMap;
    }

    public static ArrayList<Hyperlink> getSongItemHyperlinkList(VBox parent) {
        ArrayList<Hyperlink> songItemHyperlinkList = new ArrayList<>();

        // Loop over all node until find the hyperlink with the song's title
        for (Node songItemGridPane : parent.getChildren()) {
            for (Node songItemVBoxOrLabel : ((GridPane) songItemGridPane).getChildren()) {
                if (songItemVBoxOrLabel.getStyleClass().contains("item")) {
                    for (Node songItemText : ((VBox) songItemVBoxOrLabel).getChildren()) {
                        if (songItemText.getStyleClass().contains("song")) {
                            songItemHyperlinkList.add((Hyperlink) songItemText);
                        }
                    }
                }
            }
        }

        return songItemHyperlinkList;
    }

    public static void toggleSongItemColor(VBox parent, Hyperlink currentSongItemHyperlink) {
        for (Hyperlink songItemHyperlink : UIHelper.getSongItemHyperlinkList(parent)) {
            if (songItemHyperlink.getStyleClass().contains("song")) {
                // Reset the class "playing"
                songItemHyperlink.setStyle("-fx-font-family: 'SF Pro Display Semibold'");
                songItemHyperlink.getStyleClass().removeAll("playing");

                // Verify if this hyperlink is the current playing
                // So, we add the class "playing"
                if (songItemHyperlink == currentSongItemHyperlink) {
                    songItemHyperlink.setStyle("-fx-font-family: 'SF Pro Display Bold'");
                    songItemHyperlink.getStyleClass().add("playing");
                }
            }
        }
    }
}
