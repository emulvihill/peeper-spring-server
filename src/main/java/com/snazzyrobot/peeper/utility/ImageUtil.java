package com.snazzyrobot.peeper.utility;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageUtil {

    public static BufferedImage decodeBase64ToImage(String base64String) throws IOException {
        byte[] imageBytes = Base64.getDecoder().decode(base64String);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        return ImageIO.read(inputStream);
    }


    public static BufferedImage combineImagesSideBySide(BufferedImage img1, BufferedImage img2) {
        int width = img1.getWidth() + img2.getWidth();
        int height = Math.max(img1.getHeight(), img2.getHeight());

        BufferedImage combinedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();

        g.drawImage(img1, 0, 0, null);
        g.drawImage(img2, img1.getWidth(), 0, null);

        // Draw vertical line separating the two images
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(3f));
        g.drawLine(img1.getWidth() - 1, 0, img1.getWidth() - 1, height);

        g.dispose();
        return combinedImage;
    }

    public static String encodeImageToBase64(BufferedImage combined) throws IOException {
        return Base64.getEncoder().encodeToString(bufferedImageToByteArray(combined, "png"));
    }

    public static byte[] bufferedImageToByteArray(BufferedImage image, String formatName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, formatName, baos);
        baos.flush();
        byte[] imageBytes = baos.toByteArray();
        baos.close();
        return imageBytes;
    }
}
