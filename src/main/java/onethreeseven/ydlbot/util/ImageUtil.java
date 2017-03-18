package onethreeseven.ydlbot.util;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.logging.Logger;

/**
 * Useful util for dealing with images.
 * @author Luke Bermingham
 */
public class ImageUtil {

    private static final Logger logger = Logger.getLogger(ImageUtil.class.getSimpleName());

    private static final Rectangle screenDims =
            new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

    private static final Robot robot;
    static {
        Robot robo = null;
        try {
            robo = new Robot();
        } catch (AWTException e) {
            logger.severe("Could not intialise robot to get frames.");
            e.printStackTrace();
        }
        robot = robo;
    }

    public static Mat getFrame(){
        BufferedImage bi = robot.createScreenCapture(screenDims);
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }



}
