package onethreeseven.ydlbot.model;

import onethreeseven.ydlbot.util.ImageUtil;
import org.opencv.core.*;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.CvType.CV_32F;


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

    private static final FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.ORB);
    private static final DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
    private static final DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
    private static final float nndrRatio = 0.85f;

    private final Mat imageToLookFor;
    private final Mat imageDescriptor;
    private final MatOfKeyPoint imageKeyPoints;

    //private final Mat duelistDescriptor;

    public DuelistLocator(){
        //String imgPath = Thread.currentThread().getContextClassLoader().getResource("save.png").getPath();
        String imgPath = "C:\\Users\\luke\\Desktop\\projects\\ydl-bot\\src\\main\\resources\\key3.png";
        imageToLookFor = Imgcodecs.imread(imgPath, Imgcodecs.IMREAD_COLOR);
        imageKeyPoints = getKeyPoints(imageToLookFor);
        imageDescriptor = getDescriptors(imageToLookFor, imageKeyPoints);
    }

    private MatOfKeyPoint getKeyPoints(Mat image){
        MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
        featureDetector.detect(image, objectKeyPoints);
        return objectKeyPoints;
    }

    private Mat getDescriptors(Mat image, MatOfKeyPoint objectKeyPoints){
        Mat descriptor = new Mat();
        descriptorExtractor.compute(image, objectKeyPoints, descriptor);
        return descriptor;
    }

    public void findDuelist(){
        //get the duel links scene and descriptors
        Mat frame = ImageUtil.getOpenCVScreenShot();
        MatOfKeyPoint frameKeyPoints = getKeyPoints(frame);
        Mat frameDescriptors = getDescriptors(frame, frameKeyPoints);
        //find matches of the duelist image in the scene
        List<MatOfDMatch> matches = new ArrayList<>();

        imageDescriptor.convertTo(imageDescriptor, CV_32F);
        frameDescriptors.convertTo(frameDescriptors, CV_32F);

        descriptorMatcher.knnMatch(imageDescriptor, frameDescriptors, matches, 5);
        ArrayList<DMatch> prunedMatches = pruneMatches(matches);

        //go through the matches get use the indexes to get the relevant key points
        List<KeyPoint> sceneKeyPoints = frameKeyPoints.toList();

        int pixelBuf = 10;
        byte[] green = new byte[]{(byte)0, (byte)255, (byte)0};

        for (DMatch match : prunedMatches) {
            Point pt = sceneKeyPoints.get(match.trainIdx).pt;
            System.out.println(pt);

            int midX = (int) pt.x;
            int midY = (int) pt.y;
            int minX = Math.max(0, midX - pixelBuf);
            int maxX = Math.min(frame.width(), midX + pixelBuf);
            int minY = Math.max(0, midY - pixelBuf);
            int maxY = Math.min(frame.height(), midY + pixelBuf);

            for (int x = minX; x < maxX; x++) {
                for (int y = minY; y < maxY; y++) {
                    frame.put(y,x,green);
                }
            }


        }

        System.out.println(frame.cols());
        System.out.println(frame.rows());

        Imgcodecs.imwrite("C:\\Users\\luke\\Desktop\\matches.png", frame);

        System.out.println("Found matches");
    }

    private ArrayList<DMatch> pruneMatches(List<MatOfDMatch> matches){
        ArrayList<DMatch> dMatches = new ArrayList<>();

        for (MatOfDMatch match : matches) {
            DMatch[] dmatcharray = match.toArray();
            DMatch m1 = dmatcharray[0];
            DMatch m2 = dmatcharray[1];
            if(m1.distance <= m2.distance * nndrRatio){
                dMatches.add(m1);
            }
        }
        return dMatches;
    }


}
