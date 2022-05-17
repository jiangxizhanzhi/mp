package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.util.remotefile.LfsClient;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;

@Component
public class LfsCommon {

    public static LfsClient lfsClient;

    private LfsCommon(BaseAppConfig baseAppConfig) {
        LfsCommon.lfsClient = new LfsClient(baseAppConfig.getFileServerDir());
    }

    public static String upload(String localFilepath) {
        return lfsClient.upload(localFilepath);
    }

    public static String uploadContent(String content) {
        return lfsClient.uploadContent(content);
    }

    public static String uploadContent(String content, String fileSuffix) {
        return lfsClient.uploadContent(content, fileSuffix);
    }

    public static String upload(InputStream is) {
        return lfsClient.upload(is);
    }

    public static String upload(InputStream is, String fileSuffix) {
        return lfsClient.upload(is, fileSuffix);
    }

    public void download(String fileKey, OutputStream os) {
        lfsClient.download(fileKey, os);
    }

    public static boolean download(String fileKey, String localFilepath) {
        return lfsClient.download(fileKey, localFilepath);
    }

    public static String downloadTmp(String fileKey) {
        return lfsClient.downloadTmp(fileKey);
    }

    public static String downloadTmp(String fileKey, String filename) {
        return lfsClient.downloadTmp(fileKey, filename);
    }

    public static String downloadContent(String fileKey) {
        return lfsClient.downloadContent(fileKey);
    }

    public static boolean delete(String fileKey) {
        return lfsClient.delete(fileKey);
    }

}
