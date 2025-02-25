package com.iluncrypt.iluncryptapp.utils;

import java.awt.image.BufferedImage;
import java.util.Map;

public class EncryptedImageWithMetadata {
    private BufferedImage image;
    private Map<String, String> metadata;

    public EncryptedImageWithMetadata(BufferedImage image, Map<String, String> metadata) {
        this.image = image;
        this.metadata = metadata;
    }

    public BufferedImage getImage() {
        return image;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}