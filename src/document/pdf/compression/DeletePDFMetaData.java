import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.IOException;

public class DeletePDFMetaData {

    /**
     * This method Delete pdf's metadata when @param deleteMetadata true
     * flatten pdf form fields annotation
     * if false compresses metadata
     */
    public void deleteMetaData(File file, boolean deleteMetadata) throws IOException {
        try (PDDocument pdDocument = PDDocument.load(file)) {
            PDDocumentCatalog catalog = pdDocument.getDocumentCatalog();
            PDMetadata metadata = catalog.getMetadata();
            PDAcroForm pdAcroForm = catalog.getAcroForm();
            if (!ObjectUtils.isEmpty(pdAcroForm)) {
                pdAcroForm.flatten();
            }
            if (!ObjectUtils.isEmpty(metadata)) {
                if (deleteMetadata) {
                    metadata.setMetadata(null);
                } else {
                    metadata.addCompression();
                }
                catalog.setMetadata(metadata);
                pdDocument.save(file);
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
