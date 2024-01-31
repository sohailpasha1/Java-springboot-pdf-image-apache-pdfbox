import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDStream;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class PDFTextContent {
   
    /**
     * We will Flate Decode every Single page stream of pdf then after compression will put it back as it is.
     * Doing that will reduce size of pdf greatly.
     * If the Pdf already have Flate_Decode then we wont see much compression
     */
    public void compressPDFText(File inputFile, File outputFile) throws IOException {
        try (PDDocument pdDocument = PDDocument.load(inputFile)) {
            for (int i = 0; i < pdDocument.getNumberOfPages(); i++) {
                InputStream originalContent = pdDocument.getPage(i).getContents();
                byte[] originalContentBytes = IOUtils.toByteArray(originalContent);
                byte[] compressedContentBytes = compressingBytes(originalContentBytes);
                byte[] uncompressedContentBytes = uncompressBytes(compressedContentBytes);
                if (!Arrays.equals(originalContentBytes, uncompressedContentBytes)) {
                    throw new IOException("Decompressed content doesn't match original content");
                }
                ByteArrayInputStream decompressedInputStream = new ByteArrayInputStream(uncompressedContentBytes);
                PDStream compressedStream = new PDStream(pdDocument, decompressedInputStream, COSName.FLATE_DECODE);
                compressedStream.setFilters(Collections.singletonList(COSName.FLATE_DECODE));
                pdDocument.getPage(i).setContents(compressedStream);
            }
            pdDocument.save(outputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * compressing all the input bytes DeFlater
     */
    private static byte[] compressingBytes(byte[] inputBytes) throws IOException {
        try {
            Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
            deflater.setInput(inputBytes);
            deflater.finish();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(inputBytes.length);
            byte[] buffer = new byte[5120];
            while (!deflater.finished()) {
                int byteCount = deflater.deflate(buffer);
                outputStream.write(buffer, 0, byteCount);
            }
            deflater.end();
            outputStream.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * uncompressing all the input bytes DeFlater
     */
    private static byte[] uncompressBytes(byte[] inputBytes) throws IOException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(inputBytes.length);
            int bufferLength = 5120;
            byte[] buffer = new byte[bufferLength];
            Inflater inflater = new Inflater();
            inflater.setInput(inputBytes);
            while (!inflater.finished()) {
                int byteCount;
                try {
                    byteCount = inflater.inflate(buffer);
                } catch (DataFormatException e) {
                    throw new IOException("Invalid compressed data", e);
                }
                outputStream.write(buffer, 0, byteCount);
            }
            inflater.end();
            outputStream.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw e;
        }
    }
}
