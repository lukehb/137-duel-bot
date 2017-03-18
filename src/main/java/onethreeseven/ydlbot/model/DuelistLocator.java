package onethreeseven.ydlbot.model;

import onethreeseven.ydlbot.util.ImageUtil;
import org.opencv.core.*;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.ArrayList;
import java.util.List;


/**
 * Finds duelists using image recognition.
 * @author Luke Bermingham
 */
public class DuelistLocator {

//    /**
//     * The detector we use for finding duelists.
//     */
//    private final CascadeClassifier detector =
//            new CascadeClassifier(getClass().getResource("/lbpcascade_frontalface.xml").getPath());
//
//    public void findDuelist(){
//        Mat frame = ImageUtil.getOpenCVScreenShot();
//        // Detect faces in the image.
//        MatOfRect detections = new MatOfRect();
//        detector.detectMultiScale(frame, detections);
//        System.out.println(String.format("Detected %s faces", detections.toArray().length));
//    }

    private static final FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SURF);
    private static final DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SURF);
    private static final Mat duelistImg = Imgcodecs.imread(DuelistLocator.class.getResource("/lena.png").getPath());
    private static final DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
    private static final float nndrRatio = 0.7f;

    private final Mat duelistDescriptor;

    public DuelistLocator(){
        this.duelistDescriptor = getDescriptors(duelistImg);
    }

    private Mat getDescriptors(Mat image){
        MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
        featureDetector.detect(image, objectKeyPoints);
        Mat descriptor = new Mat();
        descriptorExtractor.compute(image, objectKeyPoints, descriptor);
        return descriptor;
    }

    public void findDuelist(){
        //get the duel links scene and descriptors
        Mat frame = ImageUtil.getOpenCVScreenShot();
        Mat frameDescriptors = getDescriptors(frame);
        //find matches of the duelist image in the scene
        List<MatOfDMatch> matches = new ArrayList<>();
        descriptorMatcher.knnMatch(duelistDescriptor, frameDescriptors, matches, 2);
        pruneMatches(matches);
        System.out.println("Found matches");
    }

    private void pruneMatches(List<MatOfDMatch> matches){
        matches.stream().filter(matOfDMatch -> {
            DMatch[] dmatcharray = matOfDMatch.toArray();
            DMatch m1 = dmatcharray[0];
            DMatch m2 = dmatcharray[1];
            return m1.distance <= m2.distance * nndrRatio;
        });
    }


}
