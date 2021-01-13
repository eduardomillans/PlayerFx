package controllers.interfaces;

public interface ProgressListener {

    void changed(double totalDurationInMillis, double currentDurationInMillis);

}
