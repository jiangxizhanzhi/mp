package cn.cyberpeace.mp.base.util.fdfsclient;

import java.io.InputStream;
import java.io.OutputStream;

public interface FileClient {

    String upload(String localFilepath);

    String uploadContent(String content);

    String uploadContent(String content, String fileSuffix);

    String upload(InputStream is);

    String upload(InputStream is, String fileSuffix);

    void download(String remoteFileKey, OutputStream os);

    boolean download(String remoteFileKey, String localFilepath);

    String downloadTmp(String remoteFileKey);

    String downloadContent(String remoteFileKey);

    boolean delete(String remoteFileKey);

}
