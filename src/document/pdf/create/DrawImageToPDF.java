import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DrawImageToPDF{
/**
     * Draw Image into a individual PDF File
     */
    public void drawImageToPDF(List<File> fileList) throws IOException {
        for (File file : fileList) {
            try (PDDocument pdDocument = new PDDocument()) {
                this.drawingImageAsNewPage(pdDocument, file.toPath().toString());
                pdDocument.save("D://Location" + file.getName());
            }
        }
    }

    /**
     * this method converts image to pdf
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
