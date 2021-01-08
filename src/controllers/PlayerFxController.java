package controllers;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PlayerFxController {

    private double xOffset;
    private double yOffset;
    private final String iconsPath;

    public PlayerFxController() {
        xOffset = 0.0;
        yOffset = 0.0;
        iconsPath = "src/resources/icons/";
    }

    public void handleMinimizeApp(MouseEvent mouseEvent) {
        ((Stage)(((VBox) mouseEvent.getSource()).getScene().getWindow())).setIconified(true);
    }

    public void handleCloseApp() {
        Platform.exit();
        System.exit(0);
    }

    public void handleWindowsCloseButtonEntered(MouseEvent mouseEvent) {
        try {
            Image image = new Image(new FileInputStream(iconsPath + "close_light.png"));
            ((ImageView) ((VBox) mouseEvent.getSource()).getChildren().get(0)).setImage(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void handleWindowsCloseButtonExited(MouseEvent mouseEvent) {
        try {
            Image image = new Image(new FileInputStream(iconsPath + "close.png"));
            ((ImageView) ((VBox) mouseEvent.getSource()).getChildren().get(0)).setImage(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void handleAppPressed(MouseEvent mouseEvent) {
        xOffset = (((GridPane) mouseEvent.getSource()).getScene().getWindow().getX()) - mouseEvent.getScreenX();
        yOffset = (((GridPane) mouseEvent.getSource()).getScene().getWindow().getY()) - mouseEvent.getScreenY();
    }

    public void handleAppDragged(MouseEvent mouseEvent) {
        ((GridPane) mouseEvent.getSource()).getScene().getWindow().setX(mouseEvent.getScreenX() + xOffset);
        ((GridPane) mouseEvent.getSource()).getScene().getWindow().setY(mouseEvent.getScreenY() + yOffset);
    }
}
