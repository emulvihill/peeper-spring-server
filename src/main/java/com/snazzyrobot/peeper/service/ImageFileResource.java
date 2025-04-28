package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.utility.ImageUtil;
import org.springframework.core.io.PathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ImageFileResource {
    private final PathResource resource;
    private final File tempFile;

    public ImageFileResource(String base64Image) throws IOException {
        BufferedImage image = ImageUtil.decodeBase64ToImage(base64Image);
        this.tempFile = createTempImageFile(image);
        this.resource = new PathResource(tempFile.getAbsolutePath());
    }

    public void cleanup() {
        tempFile.delete();
    }

    public PathResource getPathResource() {
        return resource;
    }

    File createTempImageFile(BufferedImage image) throws IOException {
        File tempFile = Files.createTempFile(null, ".png").toFile();
        ImageIO.write(image, "png", tempFile);
        return tempFile;
    }
}