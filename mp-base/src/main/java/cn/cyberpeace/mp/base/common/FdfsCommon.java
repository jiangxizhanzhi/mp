package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.util.fdfsclient.FdfsClient;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class FdfsCommon {

    public static FdfsClient fdfsClient;

    private FdfsCommon(BaseAppConfig baseAppConfig) {
        FdfsCommon.fdfsClient = new FdfsClient(baseAppConfig.getFdfsTrackerServers());
    }

    public static String upload(String localFilepath) {
        return fdfsClient.upload(localFilepath);
    }

    public static String uploadContent(String content) {
        return fdfsClient.uploadContent(content);
    }

    public static String upload(InputStream is) {
        return fdfsClient.upload(is);
    }

    public static void download(String fdfsFilePath, String localFilepath) {
        fdfsClient.download(fdfsFilePath, localFilepath);
    }

    public static String downloadContent(String fdfsFilePath) {
        return fdfsClient.downloadContent(fdfsFilePath);
    }

    public static void delete(String fdfsFilePath) {
        fdfsClient.delete(fdfsFilePath);
    }

}
