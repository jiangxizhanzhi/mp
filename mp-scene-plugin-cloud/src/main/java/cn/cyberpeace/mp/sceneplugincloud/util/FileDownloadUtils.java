package cn.cyberpeace.mp.sceneplugincloud.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class FileDownloadUtils {
    public static File downloadFile(String fileUrl, String targetDir) throws IOException {
        File targetDirectory = new File(targetDir);
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
        }

        File tempFile = File.createTempFile("download-", ".tmp", targetDirectory);
        FileUtils.copyURLToFile(new URL(fileUrl), tempFile);
        return tempFile;
    }
}
