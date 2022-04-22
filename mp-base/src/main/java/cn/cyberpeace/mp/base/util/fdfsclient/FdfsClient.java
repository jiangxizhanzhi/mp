package cn.cyberpeace.mp.base.util.fdfsclient;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    public static final String TMP_DIR_NAME = "fdfs";

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

    public void download(String fdfsFilepath, String localFilepath) {
        try {
            getStorageClient().download_file1(fdfsFilepath, localFilepath);
        } catch (IOException | MyException e) {
            var msg = String.format("Fdfs download %s to %s error: %s", fdfsFilepath, localFilepath,
                    e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
    }

    public String downloadTmp(String fdfsFilepath) {
        return downloadTmp(fdfsFilepath, null);
    }

    public String downloadTmp(String fdfsFilepath, String filename) {
        var tmpDir = FileUtil.getTmpDirPath();
        var tmpFdfsDir = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, tmpDir, TMP_DIR_NAME);
        if (!FileUtil.exist(tmpFdfsDir)) {
            FileUtil.mkdir(tmpFdfsDir);
        }
        if (StringUtils.isEmpty(filename)) {
            filename = FileUtil.getName(fdfsFilepath);
        }
        var tmpFilepath = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, tmpFdfsDir, filename);
        download(fdfsFilepath, tmpFilepath);
        return tmpFilepath;
    }

    public String downloadContent(String fdfsFilepath) {
        byte[] byteContent;
        try {
            byteContent = getStorageClient().download_file1(fdfsFilepath);
        } catch (IOException | MyException e) {
            var msg = String.format("Fdfs download %s content error: %s", fdfsFilepath, e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
        return new String(byteContent, StandardCharsets.UTF_8);
    }

    public void delete(String fdfsFilepath) {
        try {
            log.info("delete fdfs file,file path:{}", fdfsFilepath);
            getStorageClient().delete_file1(fdfsFilepath);
        } catch (IOException | MyException e) {
            var msg = String.format("Fdfs delete %s error: %s", fdfsFilepath, e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
    }

}
