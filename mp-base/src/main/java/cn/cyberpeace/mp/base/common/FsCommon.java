package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.util.remotefile.FileClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;

@Component
public class FsCommon {

    public static FileClient fileClient;

    public FsCommon(@Autowired BaseAppConfig baseAppConfig, @Autowired FdfsCommon fdfsCommon,
                    @Autowired LfsCommon lfsCommon) {
        if ("fdfs".equals(BeanCommon.getBean(BaseAppConfig.class).getFileServer())) {
            FsCommon.fileClient = FdfsCommon.fdfsClient;
        } else {
            FsCommon.fileClient = LfsCommon.lfsClient;
        }
    }

    public static String genHttpUrl(String fileKey) {
        if ("fdfs".equals(BeanCommon.getBean(BaseAppConfig.class).getFileServer())) {
            return FdfsCommon.genHttpUrl(fileKey);
        } else {
            return LfsCommon.genHttpUrl(fileKey);
        }
    }

    public static String genInnerHttpUrl(String fileKey) {
        if ("fdfs".equals(BeanCommon.getBean(BaseAppConfig.class).getFileServer())) {
            return FdfsCommon.genInnerHttpUrl(fileKey);
        } else {
            return LfsCommon.genInnerHttpUrl(fileKey);
        }
    }

    public static String uploadContent(String content) {
        return fileClient.uploadContent(content);
    }

    public static String uploadContent(String content, String fileSuffix) {
        return fileClient.uploadContent(content, fileSuffix);
    }

    public static String uploadRawContent(byte[] content, String fileSuffix) {
        return fileClient.uploadRawContent(content, fileSuffix);
    }

    public static String upload(String localFilepath) {
        return fileClient.upload(localFilepath);
    }

    public static String upload(InputStream is) {
        return fileClient.upload(is);
    }

    public static String upload(InputStream is, String fileSuffix) {
        return fileClient.upload(is, fileSuffix);
    }

    public void download(String fileKey, OutputStream os) {
        fileClient.download(fileKey, os);
    }

    public static boolean download(String fileKey, String localFilepath) {
        return fileClient.download(fileKey, localFilepath);
    }

    public static String downloadTmp(String fileKey) {
        return fileClient.downloadTmp(fileKey);
    }

    public static String downloadTmp(String fileKey, String filename) {
        return fileClient.downloadTmp(fileKey, filename);
    }

    public static String downloadContent(String fileKey) {
        return fileClient.downloadContent(fileKey);
    }

    public static byte[] downloadRawContent(String fileKey) {
        return fileClient.downloadRawContent(fileKey);
    }

    public static boolean delete(String fileKey) {
        return fileClient.delete(fileKey);
    }

}
