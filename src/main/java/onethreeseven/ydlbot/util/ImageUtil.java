package onethreeseven.ydlbot.util;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
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

    public static BufferedImage getScreenShot(){
        return robot.createScreenCapture(screenDims);
    }

    public static byte[] getBGRScreenShot(){
        BufferedImage bi = getScreenShot();
        DataBufferInt pixelBuf = ((DataBufferInt) bi.getRaster().getDataBuffer());
        byte[] data = new byte[3 * pixelBuf.getSize()];
        int i = 0;
        for (int[] bank : pixelBuf.getBankData()) {
            for (int pixel : bank) {
                Color c = new Color(pixel);
                data[i] = (byte)c.getBlue();
                i++;
                data[i] = (byte)c.getGreen();
                i++;
                data[i] = (byte)c.getRed();
                i++;
            }
        }
        return data;
    }

    public static Mat getOpenCVScreenShot(){
        Mat mat = new Mat(screenDims.height, screenDims.width, CvType.CV_8UC3);
        byte[] bgr = getBGRScreenShot();
        mat.put(0, 0, bgr);
        return mat;
    }

    public static File writeBGRImage(byte[] bgrPixels){
        BufferedImage bi = new BufferedImage(screenDims.width, screenDims.height, BufferedImage.TYPE_3BYTE_BGR);
        byte[] bytes = ( (DataBufferByte) bi.getRaster().getDataBuffer() ).getData();
        System.arraycopy(bgrPixels, 0, bytes, 0, bgrPixels.length);
        File outFile = new File("testImage.png");
        if(outFile.exists() && outFile.delete()){
            System.out.println("Deleted temp file before writing a new one.");
        }
        try {
            ImageIO.write(bi, "PNG", outFile);
            System.out.println("Temp file written to: " + outFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outFile;
    }


}
