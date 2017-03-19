package onethreeseven.ydlbot.model;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.opencv.imgcodecs.Imgcodecs.CV_LOAD_IMAGE_COLOR;


/**
 * Finds duelists using image recognition.
 * @author Luke Bermingham
 */
public class DuelistLocator {

    /**
     * Very heuristic skin color detection, will it work on Odion?
     */
    private static final int colR = 204;
    private static final int colG = 170;
    private static final int colB = 145;
    private static final int rangeR = 51;
    private static final int rangeG = 68;
    private static final int rangeB = 60;
    private static final int minR = colR - rangeR;
    private static final int maxR = colR + rangeR;
    private static final int minG = colG - rangeG;
    private static final int maxG = colG + rangeG;
    private static final int minB = colB - rangeB;
    private static final int maxB = colB + rangeB;
    private static final double skinPercentageThreshold = 0.1;

    private static final Scalar debugColor = new Scalar(255, 0, 0);

    private final String testFilePath;
    private final String maskFilePath;

    public DuelistLocator(){
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String path1 = "";
        String path2 = "";
        try {
            path1 = new File(loader.getResource("test_1.jpg").toURI()).getAbsolutePath();
            path2 = new File(loader.getResource("mask.png").toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        testFilePath = path1;
        maskFilePath = path2;
    }

    public void findDuelist(){

        //keep the raw image (in color) because we do skin matching on it
        Mat raw = Imgcodecs.imread(testFilePath, CV_LOAD_IMAGE_COLOR);
        //mask out most of the screen, only want duelist area
        Mat mask = Imgcodecs.imread(maskFilePath, CV_LOAD_IMAGE_COLOR);
        Size dims = new Size(raw.cols(), raw.rows());
        Mat masked = new Mat(dims, CvType.CV_8UC3);
        raw.copyTo(masked, mask);
        //don't need raw anymore
        raw.release();
        //convert a copy of it to gray-scale for circle detection
        Mat scene = new Mat(dims, CvType.CV_8UC1);
        Imgproc.cvtColor(masked, scene, Imgproc.COLOR_BGR2GRAY);
        //run it through a strong threshold to remove nearly everything except bright objects
        Imgproc.threshold(scene, scene, 225, 255, Imgproc.THRESH_BINARY);

        //find circles
        Mat circles = new Mat();
        Imgproc.HoughCircles(scene, circles,
                Imgproc.CV_HOUGH_GRADIENT,
                1, 30, 250, 20, 10, 70);

        //turn on if you want to see debug circles
        //drawDebugCircles(scene, circles);
        //go through each circle we found and decide whether to keep it or not
        ArrayList<double[]> goodCircles = pruneCircles(masked, circles);
        for (double[] goodCircle : goodCircles) {
            drawDebugCircle(masked, goodCircle);
        }
        // Save the visualized detection.
        File debugOutputFile = new File("debug_duelists.png");
        Imgcodecs.imwrite(debugOutputFile.getAbsolutePath(), masked);
        System.out.println("Collect debug file at: " + debugOutputFile.getAbsolutePath());
    }

    private void drawDebugCircle(Mat image, double[] circle){
        double x = circle[0];
        double y = circle[1];
        int radius = (int)circle[2];
        Imgproc.circle(image, new Point(x,y), radius, debugColor, 1);
    }

    private ArrayList<double[]> pruneCircles(Mat raw, Mat circles){
        final int width = raw.cols();
        final int height = raw.rows();
        final byte[] bgrColor = new byte[3];

        //store circles in array {x,y,radius}
        ArrayList<double[]> goodCircles = new ArrayList<>();

        for(int i = 0; i < circles.cols(); i++) {
            //x,y,radius
            double[] circle = circles.get(0, i);
            int centerX = (int) circle[0];
            int centerY = (int) circle[1];
            int radius = (int)circle[2];

            //go through pixels and check if the threshold for skin colored pixels is met
            int skinColoredPixelsFound = 0;
            int totalPixelsExamined = 0;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int offsetX = x - centerX;
                    int offsetY = y - centerY;
                    //only do circle indices
                    if (offsetX*offsetX + offsetY*offsetY <= radius*radius) {
                        raw.get(y,x,bgrColor);
                        int b = bgrColor[0] + 128;
                        int g = bgrColor[1] + 128;
                        int r = bgrColor[2] + 128;



                        if(r <= maxR && r >= minR &&
                                g <= maxG && g >= minG &&
                                b <= maxB && b >= minB){
                            skinColoredPixelsFound++;
                        }
                        totalPixelsExamined++;
                    }
                }
            }

            double percentageSkin = skinColoredPixelsFound/(double)totalPixelsExamined;
            if(percentageSkin >= skinPercentageThreshold){
                goodCircles.add(circle);
            }
        }
        return goodCircles;
    }


}
