import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.ScreenSize;

public class PlayerFxApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("resources/layouts/playerfx.fxml"));
        stage.setScene(new Scene(root, ScreenSize.getScreenWidth(), ScreenSize.getScreenHeight()));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
