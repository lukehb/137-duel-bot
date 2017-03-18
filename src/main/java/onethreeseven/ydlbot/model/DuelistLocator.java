package onethreeseven.ydlbot.model;

import onethreeseven.ydlbot.util.ImageUtil;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;

/**
 * Finds duelists using image recognition.
 * @author Luke Bermingham
 */
public class DuelistLocator {

    /**
     * The detector we use for finding duelists.
     */
    private final CascadeClassifier detector =
            new CascadeClassifier(getClass().getResource("/lbpcascade_frontalface.xml").getPath());

    public void findDuelist(){
        Mat frame = ImageUtil.getFrame();
        // Detect faces in the image.
        MatOfRect detections = new MatOfRect();
        detector.detectMultiScale(frame, detections);
        System.out.println(String.format("Detected %s faces", detections.toArray().length));
    }

}
