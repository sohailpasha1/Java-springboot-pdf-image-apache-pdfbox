import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

public class ImageCompression {

   /**
     * Image compression creating original buffered image & newBufferedImage.
     * creating outputFile compressed image.
     **/
    public File compressImage(MultipartFile multipartFile, String extension) {
        File outputFile = new File("D://Location/" + multipartFile.getOriginalFilename() + "." + extension);
        try (BufferedInputStream bis = new BufferedInputStream(multipartFile.getInputStream()); BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            BufferedImage bufferedImage = ImageIO.read(bis);
            BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            setGraphics(bufferedImage, newBufferedImage);
            ImageWriter imageWriter = getImageWriter();
            processBuffImage(imageWriter, bos, newBufferedImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outputFile;
    }

    /**
     * processing compressing bufferimage and writing to output given file.
     **/
    private void processBuffImage(ImageWriter imageWriter, BufferedOutputStream bos, BufferedImage newBufferedImage) {
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(bos)) {
            imageWriter.setOutput(ios);
            ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
            setImageQuality(imageWriteParam);
            imageWriter.write(null, new IIOImage(newBufferedImage, null, null), imageWriteParam);
            ios.flush();
            bos.flush();
            newBufferedImage.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * create JPEG image writer
     **/
    private ImageWriter getImageWriter() {
        Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName("JPEG");
        if (!imageWriters.hasNext()) {
            throw new IllegalStateException("IMAGE_WRITES_NOT_FOUND");
        }
        ImageWriter imageWriter = imageWriters.next();
        return imageWriter;
    }

    /**
     * creating Graphics for on newBufferedImage by using orignial bufferedimage
     **/
    private void setGraphics(BufferedImage bufferedImage, BufferedImage newBufferedImage) {
        RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        Graphics2D graphics = newBufferedImage.createGraphics();
        graphics.setRenderingHints(renderingHints);
        graphics.drawImage(bufferedImage, 0, 0, newBufferedImage.getWidth(), newBufferedImage.getHeight(), Color.WHITE, null);
        bufferedImage.flush();
        graphics.dispose();
    }

    /**
     * Setting up image compression quality factor(0.3F to 0.7F)
     **/
    private void setImageQuality(ImageWriteParam imageWriteParam) {
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionType("JPEG");
        imageWriteParam.setCompressionQuality(0.4F);
    }
}
