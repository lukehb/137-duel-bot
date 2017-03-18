package onethreeseven.ydlbot.util;

import java.awt.*;
import java.awt.image.BufferedImage;
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

    public static BufferedImage getFrame(){
        return robot.createScreenCapture(screenDims);
    }

}
