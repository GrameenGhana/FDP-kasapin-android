package org.grameen.fdp.kasapin.utilities;

import java.io.File;
import java.io.FileOutputStream;

import timber.log.Timber;

import static org.grameen.fdp.kasapin.utilities.AppConstants.ROOT_DIR;

public class FileUtils {
    public static void createNoMediaFile() {
        FileOutputStream out;
        try {
            createFolder(".thumbnails", null);
            File file = new File(AppConstants.ROOT_DIR + File.separator + ".nomedia");
            if (!file.exists()) {
                out = new FileOutputStream(file);
                out.write(0);
                out.close();
                AppLogger.i("FileUtils", "No media file created!  " + file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static File createFolder(String directoryName, String fileName) {
        String dir = ROOT_DIR + "/" + directoryName + "/";
        File file = new File(dir);
        if (!file.exists())
            file.mkdirs();
        if (fileName == null)
            return file;
        return new File(dir, fileName);
    }

    public static File createTemporaryFile(String part, String ext) throws Exception {
        File dir = new File(ROOT_DIR + File.separator + ".temp/");
        if (!dir.exists()) Timber.i("Is DIR created?  %s", dir.mkdirs());
        return File.createTempFile(part, ext, dir);
    }
}
