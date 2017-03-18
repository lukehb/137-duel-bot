package onethreeseven.ydlbot.util;

import org.junit.Assert;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;

import static org.junit.Assert.*;

/**
 * Testing the {@link ImageUtil}.
 * @author Luke Bermingham
 */
public class ImageUtilTest {
    @Test
    public void writeBGRImage() throws Exception {
        byte[] bgrScreenshot = ImageUtil.getBGRScreenShot();
        File outImage = ImageUtil.writeBGRImage(bgrScreenshot);
        Assert.assertTrue(outImage.exists());
    }

}