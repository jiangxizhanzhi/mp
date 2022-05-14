package cn.cyberpeace.mp.base.util.fdfsclient;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.hutool.core.io.FileUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.csource.common.MyException;
import org.csource.fastdfs.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class FdfsClient implements FileClient {

    public static final String TMP_DIR_NAME = "fdfs";

    private final GenericObjectPool<TrackerServer> connectionPool;

    private Connection getConnection() {
        TrackerServer trackerServer;
        try {
            trackerServer = connectionPool.borrowObject();
        } catch (Exception e) {
            var msg = String.format("Fdfs get connection error: %s", e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
        return new Connection(connectionPool, trackerServer);
    }

    public static class FdfsClientBuilder {
        private List<String> trackerServers;
        private Integer maxTotalConnection;

        public FdfsClientBuilder configServers(List<String> trackerServers) {
            this.trackerServers = trackerServers;
            return this;
        }

        public FdfsClientBuilder configMaxTotalConnection(Integer maxTotalConnection) {
            this.maxTotalConnection = maxTotalConnection;
            return this;
        }

        public FdfsClient build() {
            return new FdfsClient(trackerServers, maxTotalConnection);
        }
    }

    public static FdfsClientBuilder builder() {
        return new FdfsClientBuilder();
    }

    private FdfsClient(List<String> trackerServers, Integer maxTotalConnection) {
        var trackerClient = new TrackerClient(new TrackerGroup(trackerServers.stream()
                .map(trackerServer -> {
            String[] arr2 = trackerServer.trim().split(":");
            String host = arr2[0].trim();
            var port = Integer.parseInt(arr2[1].trim());
            return new InetSocketAddress(host, port);
        }).toArray(InetSocketAddress[]::new)));

        var poolConfig = new GenericObjectPoolConfig();
        if (maxTotalConnection != null) {
            poolConfig.setMaxTotal(maxTotalConnection);
        }
        connectionPool = new GenericObjectPool<>(new ConnectionFactory(trackerClient), poolConfig);
    }

    public String upload(String localFilepath) {
        try (var connection = getConnection()) {
            return connection.getStorageClient().upload_file1(localFilepath, null, null);
        } catch (IOException | MyException e) {
            var msg = String.format("Fdfs upload local file %s error: %s", localFilepath, e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
    }

    public String uploadContent(String content) {
        return uploadContent(content, null);
    }

    public String uploadContent(String content, String fileSuffix) {
        return upload(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), fileSuffix);
    }

    public String upload(InputStream is) {
        return upload(is, null);
    }

    public String upload(InputStream is, String fileSuffix) {
        String fdfsFilePath;
        try (is; var connection = getConnection()) {
            var filesize = is.available();
            fdfsFilePath = connection.getStorageClient().upload_file1(null, filesize,
                    new UploadStream(is, filesize), fileSuffix, null);
        } catch (IOException | MyException e) {
            var msg = String.format("Fdfs upload with input stream error: %s", e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
        return fdfsFilePath;
    }

    public void download(String fdfsFilepath, OutputStream os) {
        try (var connection = getConnection()) {
            connection.getStorageClient().download_file1(fdfsFilepath, new DownloadStream(os));
        } catch (IOException | MyException e) {
            var msg = String.format("Fdfs download %s to output stream error: %s", fdfsFilepath, e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
    }

    public boolean download(String fdfsFilepath, String localFilepath) {
        try (var connection = getConnection()) {
            connection.getStorageClient().download_file1(fdfsFilepath, localFilepath);
        } catch (IOException | MyException e) {
            var msg = String.format("Fdfs download %s to %s error: %s", fdfsFilepath, localFilepath,
                    e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
        return true;
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
        try (var connection = getConnection()) {
            byteContent = connection.getStorageClient().download_file1(fdfsFilepath);
        } catch (IOException | MyException e) {
            var msg = String.format("Fdfs download %s content error: %s", fdfsFilepath, e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
        return new String(byteContent, StandardCharsets.UTF_8);
    }

    public boolean delete(String fdfsFilepath) {
        try (var connection = getConnection()) {
            log.info("delete fdfs file, file path: {}", fdfsFilepath);
            connection.getStorageClient().delete_file1(fdfsFilepath);
        } catch (IOException | MyException e) {
            var msg = String.format("Fdfs delete %s error: %s", fdfsFilepath, e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
        return true;
    }

    public static class Connection implements AutoCloseable {

        private final GenericObjectPool<TrackerServer> connectionPool;

        private final TrackerServer trackerServer;

        @Getter
        private final StorageClient1 storageClient;

        public Connection(GenericObjectPool<TrackerServer> connectionPool, TrackerServer trackerServer) {
            this.connectionPool = connectionPool;
            this.trackerServer = trackerServer;
            this.storageClient = new StorageClient1(trackerServer, null);
        }

        @Override
        public void close() {
            connectionPool.returnObject(trackerServer);
        }

    }

    public static class ConnectionFactory extends BasePooledObjectFactory<TrackerServer> {

        @Getter
        private final TrackerClient trackerClient;

        public ConnectionFactory(TrackerClient trackerClient) {
            this.trackerClient = trackerClient;
        }

        @Override
        public TrackerServer create() throws Exception {
            try {
                return trackerClient.getConnection();
            } catch (Exception e) {
                log.error("Fdfs connect to tracker server error: ", e);
                throw e;
            }
        }

        @Override
        public PooledObject<TrackerServer> wrap(TrackerServer trackerServer) {
            return new DefaultPooledObject<>(trackerServer);
        }

        @Override
        public void destroyObject(PooledObject<TrackerServer> pooledObject) throws Exception {
            var trackerServer = pooledObject.getObject();
            if (trackerServer != null) {
                trackerServer.getInputStream().close();
                trackerServer.getOutputStream().close();
                trackerServer.close();
            }
        }

    }
}
