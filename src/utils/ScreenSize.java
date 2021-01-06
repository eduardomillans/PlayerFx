package utils;

import javafx.stage.Screen;

public class ScreenSize {

    public static double getScreenWidth() {
        double originalScreenWidth = Screen.getPrimary().getBounds().getWidth();
        return originalScreenWidth - (originalScreenWidth * 0.3);
    }

    public static double getScreenHeight() {
        double originalScreenHeight = Screen.getPrimary().getBounds().getHeight();
        return originalScreenHeight - (originalScreenHeight * 0.3);
    }

}
