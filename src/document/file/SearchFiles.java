public class SearchFiles{
  
   public static List<File> captureFiles = new LinkedList<>();
  
    /**
     * for the Given Directory searches the file in directory and its sub-directories so-on
     */
    private static void captureFilesFromDirectoryTree(File directory) {
        try {
          File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                            captureFiles.add(file);
                    } else if (file.isDirectory()) {
                        captureFilesFromDirectoryTree(file);
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
