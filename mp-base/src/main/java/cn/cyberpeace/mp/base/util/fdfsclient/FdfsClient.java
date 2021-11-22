package cn.cyberpeace.mp.base.util.fdfsclient;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.csource.common.MyException;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerGroup;
import org.csource.fastdfs.TrackerServer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class FdfsClient {

    private final TrackerClient trackerClient;

    /**
     * TODO 优化连接池
     */
    private StorageClient1 getStorageClient() {
        TrackerServer trackerServer;
        try {
            trackerServer = trackerClient.getConnection();
        } catch (IOException e) {
            var msg = String.format("Fdfs connect to tracker server error: %s", e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
        return new StorageClient1(trackerServer, null);
    }

    public FdfsClient(List<String> trackerServers) {
        trackerClient = new TrackerClient(new TrackerGroup(trackerServers.stream()
                .map(trackerServer -> {
            String[] arr2 = trackerServer.trim().split(":");
            String host = arr2[0].trim();
            var port = Integer.parseInt(arr2[1].trim());
            return new InetSocketAddress(host, port);
        }).toArray(InetSocketAddress[]::new)));
    }

    public String upload(String localFilepath) {
        try {
            return getStorageClient().upload_file1(localFilepath, null, null);
        } catch (IOException | MyException e) {
            var msg = String.format("Fdfs upload local file %s error: %s", localFilepath, e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
    }

    public String uploadContent(String content) {
        return upload(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    public String upload(InputStream is) {
        String fdfsFilePath;
        try (is) {
            var fileBuff = new byte[is.available()];
            is.read(fileBuff, 0, fileBuff.length);
            fdfsFilePath = getStorageClient().upload_file1(fileBuff, null, null);
        } catch (IOException | MyException e) {
            var msg = String.format("Fdfs upload with input stream error: %s", e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }

        return fdfsFilePath;
    }

    public void download(String fdfsFilePath, String localFilepath) {
        try {
            getStorageClient().download_file1(fdfsFilePath, localFilepath);
        } catch (IOException | MyException e) {
            var msg = String.format("Fdfs download %s to %s error: %s", fdfsFilePath, localFilepath,
                    e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
    }

    public String downloadContent(String fdfsFilePath) {
        byte[] byteContent;
        try {
            byteContent = getStorageClient().download_file1(fdfsFilePath);
        } catch (IOException | MyException e) {
            var msg = String.format("Fdfs download %s content error: %s", fdfsFilePath, e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
        return new String(byteContent, StandardCharsets.UTF_8);
    }

    public void delete(String fdfsFilePath) {
        try {
            getStorageClient().delete_file1(fdfsFilePath);
        } catch (IOException | MyException e) {
            var msg = String.format("Fdfs delete %s error: %s", fdfsFilePath, e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
    }

}
