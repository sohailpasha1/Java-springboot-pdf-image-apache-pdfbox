import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class GeneratePDF {
    private float Y_AXIS;
    private float MARGIN;
    private PDDocument document;

    /**
     * Generate new PDF File
     * Drawing images on page
     * writing text on page
     * Font style and color
     */
    public void generatePDFFile() {
        try {
            MARGIN = 25.0F;
            Y_AXIS = 720f;
            File generatedPDF = new File("D://Location/Demo-" + System.nanoTime() + ".pdf");
            document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            this.drawImagesOnPDFPage(contentStream);
            Y_AXIS -= 100;
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12.0F);
            this.addText(contentStream, "Hello World", 100, Y_AXIS);
            Y_AXIS -= 100;
            contentStream.setFont(PDType1Font.HELVETICA_BOLD_OBLIQUE, 10.0F);
            contentStream.setStrokingColor(Color.lightGray);
            contentStream.stroke();
            this.addText(contentStream, "APACHE PDF BOX", 100, Y_AXIS);
            contentStream.close();
            document.save(generatedPDF);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  /**
  *  Drawing image on pdf page at diffrent position and sizes
  **/
    private void drawImagesOnPDFPage(PDPageContentStream contentStream) throws IOException {
        try {
            InputStream image1Stream = PDFService.class.getResourceAsStream("/img1.png");
            InputStream image2Stream = PDFService.class.getResourceAsStream("/img2.png");
            InputStream image3Stream = PDFService.class.getResourceAsStream("/img3.png");
            PDImageXObject xObject1 = PDImageXObject.createFromByteArray(document, IOUtils.toByteArray(image1Stream), "image1");
            PDImageXObject xObject2 = PDImageXObject.createFromByteArray(document, IOUtils.toByteArray(image2Stream), "image2");
            PDImageXObject xObject3 = PDImageXObject.createFromByteArray(document, IOUtils.toByteArray(image3Stream), "image3");
            //drawing image on left top corner
            contentStream.drawImage(xObject1, MARGIN - 2.0F, Y_AXIS, 190.0F, 80.0F);
            //drawing image on right top corner
            contentStream.drawImage(xObject2, 0f, 0f, PDRectangle.A4.getWidth(), 60f);
            //drawing image on bottom
            contentStream.drawImage(xObject3, PDRectangle.A4.getWidth() - 120.0F, PDRectangle.A4.getHeight() - 123f, 100.0F, 123f);
        } catch (Exception e) {
            throw e;
        }
    }

  /**
  *   code to write text on pdf page
  **/
    private void addText(PDPageContentStream contentStream, String text, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }
}
