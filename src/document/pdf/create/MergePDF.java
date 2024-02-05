package com.jocata.boot;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MergePDF {
    /**
     * This method merges 2 or more document files into a Single PDF file
     */
    public void mergeDocuments(List<File> fileList, File outputFile) throws IOException {
        try {
            PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
            pdfMergerUtility.setDestinationFileName(outputFile.getAbsolutePath());
            int fileCount = 0;
            for (File file : fileList) {
                String extension = FilenameUtils.getExtension(file.getName());
                if (!"PDF".equalsIgnoreCase(extension)) {
                    try (PDDocument pdDocument = new PDDocument()) {
                        drawingImageAsNewPage(pdDocument, file.toPath().toString());
                        pdDocument.save(file);
                    }
                    pdfMergerUtility.addSource(outputFile);
                } else {
                    pdfMergerUtility.addSource(file);
                }
            }
            pdfMergerUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());

        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * This method
     * converts image to pdf
     */
    public void drawingImageAsNewPage(PDDocument doc, String imagePath) throws IOException {
        PDImageXObject image = PDImageXObject.createFromFile(imagePath, doc);
        PDRectangle pageSize = PDRectangle.A4;
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();
        float pageWidth = pageSize.getWidth();
        float pageHeight = pageSize.getHeight();
        float ratio = Math.min(pageWidth / originalWidth, pageHeight / originalHeight);
        float scaledWidth = originalWidth * ratio;
        float scaledHeight = originalHeight * ratio;
        float x = (pageWidth - scaledWidth) / 2;
        float y = (pageHeight - scaledHeight) / 2;
        PDPage page = new PDPage(pageSize);
        doc.addPage(page);
        try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
            contents.drawImage(image, x, y, scaledWidth, scaledHeight);
        } catch (IOException e) {
            throw e;
        }
    }
}
