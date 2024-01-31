import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PDFEmbeddedImage {

    /**
     * iterates every single pdf page find the images pdResource objects.
     * and improve pdf version.
     */
    public void compressPDFEmbeddedImages(MultipartFile inputMultipartFile, File outputFile) throws IOException {
        try (PDDocument pdDocument = PDDocument.load(inputMultipartFile.getInputStream())) {
            pdDocument.setAllSecurityToBeRemoved(true);
            for (int pageIndex = 0; pageIndex < pdDocument.getNumberOfPages(); pageIndex++) {
                PDPage pdPage = pdDocument.getPage(pageIndex);
                PDResources pdResources = pdPage.getResources();
                if (ObjectUtils.isEmpty(pdResources)) {
                    continue;
                }
                compressingImage(pdResources, pdDocument);
            }
            improvePDFVersion(pdDocument);
            pdDocument.save(outputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * if pdf version less than 1.5F increase the pdf version.
     */
    private void improvePDFVersion(PDDocument pdDocument) {
        pdDocument.getDocumentCatalog().setStructureTreeRoot(null);
        pdDocument.getDocument().setIsXRefStream(true);
        if (pdDocument.getVersion() < 1.5f) {
            pdDocument.setVersion(1.5f);
        }
    }

    /**
     * on each page will compress all the images and replace it wil the compressed one.
     */
    private void compressingImage(PDResources pdResources, PDDocument pdDocument) throws IOException {
        Iterable<COSName> cosNames = pdResources.getXObjectNames();
        for (COSName cosName : cosNames) {
            PDXObject pdxObject = pdResources.getXObject(cosName);
            if (ObjectUtils.isEmpty(pdxObject))
                continue;
            PDStream pdStream = pdxObject.getStream();
            pdStream.getCOSObject().setNeedToBeUpdated(true);
            pdStream.addCompression();
            PDImageXObject pdImageXObject = new PDImageXObject(pdStream, pdResources);
            try {
                BufferedImage bufferedImage = !ObjectUtils.isEmpty(pdImageXObject.getImage()) ? pdImageXObject.getImage() : pdImageXObject.getOpaqueImage();
                processCompression(bufferedImage, pdResources, cosName, pdDocument);
            } catch (Exception e) {
                System.out.println("Exception occurred while optimizing image, Exception is: " + e);
                continue;
            }
        }
    }

    /**
     * compressing the image with 0.5 quality factor
     */
    private void processCompression(BufferedImage bufferedImage, PDResources pdResources, COSName cosName, PDDocument pdDocument) throws IOException {
        RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = newBufferedImage.createGraphics();
        graphics.setRenderingHints(renderingHints);
        graphics.drawImage(bufferedImage, 0, 0, newBufferedImage.getWidth(), newBufferedImage.getHeight(), Color.WHITE, null);
        graphics.dispose();
        PDImageXObject newImage = JPEGFactory.createFromImage(pdDocument, newBufferedImage, 0.5F);
        pdResources.put(cosName, newImage);
    }
}
