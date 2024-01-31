import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;

public class ZipArchive {
    /**
     * Use to zip one or more file
     * used compression method deflated and compression level 8
     */
    public static Path zipArchive(List<File> fileList) throws IOException {
        try {
            File outputFile = new File("D://location.zip");
            try (FileOutputStream outputStream = new FileOutputStream(outputFile);
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
                 ZipArchiveOutputStream archiveOutputStream = new ZipArchiveOutputStream(bufferedOutputStream)) {
                archiveOutputStream.setEncoding("UTF-8");
                archiveOutputStream.setMethod(ZipEntry.DEFLATED);
                archiveOutputStream.setLevel(Deflater.DEFLATED);
                for (File file : fileList) {
                    ZipArchiveEntry archiveEntry = new ZipArchiveEntry(file, createFilename(file.getName()));
                    archiveOutputStream.putArchiveEntry(archiveEntry);
                    try (FileInputStream inputStream = new FileInputStream(file)) {
                        IOUtils.copy(inputStream, archiveOutputStream);
                        archiveOutputStream.closeArchiveEntry();
                    }
                }
                archiveOutputStream.finish();
            }
            return outputFile.toPath();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * to generate file name uses original file name appends nanotime at suffix
     */
    private static String createFilename(String absoluteName) {
        try {
            String[] arrOfNameExtension = absoluteName.split("[" + "." + "]");
            StringBuilder b = new StringBuilder();
            b.append(arrOfNameExtension[0]);
            b.append("-");
            b.append("CT").append(System.nanoTime());
            b.append(".");
            b.append(arrOfNameExtension[1]);
            return b.toString();
        } catch (Exception e) {
            throw e;
        }
    }
    
}
