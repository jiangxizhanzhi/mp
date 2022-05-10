package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.util.fdfsclient.FdfsClient;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class FdfsCommon {

    public static FdfsClient fdfsClient;

    private FdfsCommon(BaseAppConfig baseAppConfig) {
        FdfsCommon.fdfsClient = FdfsClient.builder()
                .configServers(baseAppConfig.getFdfsTrackerServers())
                .build();
    }

    public static String upload(String localFilepath) {
        return fdfsClient.upload(localFilepath);
    }

    public static String uploadContent(String content) {
        return fdfsClient.uploadContent(content);
    }

    public static String uploadContent(String content, String fileSuffix) {
        return fdfsClient.uploadContent(content, fileSuffix);
    }

    public static String upload(InputStream is) {
        return fdfsClient.upload(is);
    }

    public static String upload(InputStream is, String fileSuffix) {
        return fdfsClient.upload(is, fileSuffix);
    }

    public static void download(String fdfsFilepath, String localFilepath) {
        fdfsClient.download(fdfsFilepath, localFilepath);
    }

    public static String downloadTmp(String fdfsFilepath) {
        return fdfsClient.downloadTmp(fdfsFilepath);
    }

    public static String downloadTmp(String fdfsFilepath, String filename) {
        return fdfsClient.downloadTmp(fdfsFilepath, filename);
    }

    public static String downloadContent(String fdfsFilepath) {
        return fdfsClient.downloadContent(fdfsFilepath);
    }

    public static void delete(String fdfsFilepath) {
        fdfsClient.delete(fdfsFilepath);
    }

}
