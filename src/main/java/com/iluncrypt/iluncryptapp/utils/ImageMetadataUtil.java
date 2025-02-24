package com.iluncrypt.iluncryptapp.utils;

import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility class for saving and reading metadata in PNG images.
 * This class only supports PNG, as encrypted images are always saved in PNG format.
 */
public class ImageMetadataUtil {

    /**
     * Saves a PNG image to the specified file, embedding the given metadata (as key/value pairs)
     * in the tEXt chunks of the PNG metadata.
     *
     * @param file    the file to save the image to.
     * @param image   the BufferedImage to save.
     * @param metaMap a map containing metadata entries (e.g., "iv", "tag", "extraPadding") with Base64-encoded values.
     * @throws IOException if an error occurs during saving.
     */
    public static void saveImageWithMetadata(File file, BufferedImage image, Map<String, String> metaMap) throws IOException {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
        ImageWriteParam writeParam = writer.getDefaultWriteParam();
        ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(image.getType());
        IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);

        // Update the metadata with the provided map for PNG.
        metadata = updatePNGMetadata(metadata, metaMap);

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(file)) {
            writer.setOutput(ios);
            IIOImage iioImage = new IIOImage(image, null, metadata);
            writer.write(null, iioImage, writeParam);
        }
        writer.dispose();
    }

    /**
     * Reads metadata from a PNG image file.
     *
     * @param file the PNG file from which to read metadata.
     * @return a map containing metadata entries with keys and values.
     * @throws IOException if an error occurs during reading.
     */
    public static Map<String, String> readMetadataFromImage(File file) throws IOException {
        Map<String, String> metaMap = new HashMap<>();
        ImageWriter writer = null;
        // Use a PNG ImageReader.
        var readers = ImageIO.getImageReadersByFormatName("png");
        if (!readers.hasNext()) {
            throw new IOException("No PNG ImageReader found.");
        }
        var reader = readers.next();
        try (var iis = ImageIO.createImageInputStream(file)) {
            reader.setInput(iis, true);
            IIOMetadata metadata = reader.getImageMetadata(0);
            String nativeFormat = metadata.getNativeMetadataFormatName();
            Node root = metadata.getAsTree(nativeFormat);
            NodeList textNodes = ((IIOMetadataNode) root).getElementsByTagName("tEXtEntry");
            for (int i = 0; i < textNodes.getLength(); i++) {
                IIOMetadataNode node = (IIOMetadataNode) textNodes.item(i);
                String keyword = node.getAttribute("keyword");
                String value = node.getAttribute("value");
                metaMap.put(keyword, value);
            }
        }
        reader.dispose();
        return metaMap;
    }

    /**
     * Updates the PNG metadata with the given metadata map.
     *
     * @param metadata the original IIOMetadata.
     * @param metaMap  a map of metadata entries (key/value pairs).
     * @return the updated IIOMetadata.
     * @throws IOException if an error occurs during updating.
     */
    private static IIOMetadata updatePNGMetadata(IIOMetadata metadata, Map<String, String> metaMap) throws IOException {
        String nativeFormat = metadata.getNativeMetadataFormatName(); // typically "javax_imageio_png_1.0"
        IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(nativeFormat);
        IIOMetadataNode textNode = getChildNode(root, "tEXt");
        if (textNode == null) {
            textNode = new IIOMetadataNode("tEXt");
            root.appendChild(textNode);
        }
        for (Map.Entry<String, String> entry : metaMap.entrySet()) {
            IIOMetadataNode textEntry = new IIOMetadataNode("tEXtEntry");
            textEntry.setAttribute("keyword", entry.getKey());
            textEntry.setAttribute("value", entry.getValue());
            textNode.appendChild(textEntry);
        }
        try {
            metadata.mergeTree(nativeFormat, root);
        } catch (Exception e) {
            throw new IOException("Error merging PNG metadata: " + e.getMessage(), e);
        }
        return metadata;
    }

    /**
     * Returns the first child node with the specified name.
     *
     * @param n    the parent node.
     * @param name the name of the child node to search for.
     * @return the child IIOMetadataNode, or null if not found.
     */
    private static IIOMetadataNode getChildNode(Node n, String name) {
        Node child = n.getFirstChild();
        while (child != null) {
            if (child.getNodeName().equalsIgnoreCase(name)) {
                return (IIOMetadataNode) child;
            }
            child = child.getNextSibling();
        }
        return null;
    }
}
