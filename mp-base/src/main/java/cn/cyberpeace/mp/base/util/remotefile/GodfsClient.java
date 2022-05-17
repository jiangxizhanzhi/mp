package cn.cyberpeace.mp.base.util.remotefile;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import cn.cyberpeace.mp.base.util.http.SnakeHttpParam;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.InputStreamBody;

import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
public class GodfsClient implements FileClient {

    public static final String STATUS_OK = "ok";

    public static final String TMP_DIR_NAME = "godfs";

    public static final String UPLOAD_URL = "/godfs/upload";
    public static final String DELETE_URL = "/group1/delete";
    public static final String DOWNLOAD_URL_PREFIX = "/godfs";

    @Getter
    private HttpClient godfsHttpClient;

    public static class GdfsClientBuilder {
        private String server;
        private Integer timeout;
        private Integer maxConnPerRoute;
        private Integer maxConnTotal;

        public GdfsClientBuilder configServer(String server) {
            this.server = server;
            return this;
        }

        public GdfsClientBuilder configTimeout(Integer timeout) {
            this.timeout = timeout;
            return this;
        }

        public GdfsClientBuilder configMaxConnPerRoute(Integer maxConnPerRoute) {
            this.maxConnPerRoute = maxConnPerRoute;
            return this;
        }

        public GdfsClientBuilder configMaxConnTotal(Integer maxConnTotal) {
            this.maxConnTotal = maxConnTotal;
            return this;
        }

        public GodfsClient build() {
            return new GodfsClient(server, timeout, maxConnPerRoute, maxConnTotal);
        }
    }

    public static GdfsClientBuilder builder() {
        return new GdfsClientBuilder();
    }

    private GodfsClient(String server, Integer timeout, Integer maxConnPerRoute, Integer maxConnTotal) {
        this.godfsHttpClient = HttpClient.builder()
                .configServer(server)
                .configTimeout(timeout)
                .configMaxConnPerRoute(maxConnPerRoute)
                .configMaxConnTotal(maxConnTotal)
                .build();
        this.godfsHttpClient.setLogRequest(true);
    }

    public String upload(InputStream is, long filesize, String fileSuffix) {
        var filename = TextUtils.uuid();
        if (!StringUtils.isEmpty(fileSuffix)) {
            filename = StringUtils.joinWith(".", filename, fileSuffix);
        }
        var uploadData = new GodfsUploadData();
        uploadData.setFilename(filename);
        uploadData.setFile(new InputStreamBody(is, ContentType.DEFAULT_BINARY, filename));
        var httpResult = godfsHttpClient.post(UPLOAD_URL, uploadData, null, null, null);
        if (!httpResult.isSuccess()) {
            var msg = String.format("Godfs upload file with input stream error: %s", httpResult.getResult());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }

        GodfsUploadResponse response = httpResult.snakeGetResultBean(new TypeReference<GodfsUploadResponse>() {});
        if (response == null) {
            var msg = String.format("Godfs upload file with input stream parse result(%s) failed",
                    httpResult.getResult());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }

        if (!STATUS_OK.equals(response.getStatus())) {
            var msg = String.format("Godfs upload file with input stream error: %s, result(%s)",
                    response.getMessage(), httpResult.getResult());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }

        return response.getData().getPath();
    }

    public void download(String godfsFilepath, OutputStream os) {
        var downloadUrl = StringUtils.join(DOWNLOAD_URL_PREFIX, godfsFilepath);
        var httpResult = godfsHttpClient.download(os, downloadUrl, null, null, null);
        if (httpResult.getException() != null) {
            var msg = String.format("Godfs download %s to output stream error: %s", godfsFilepath,
                    httpResult.getException().getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
    }

    public boolean delete(String godfsFilepath) {
        var deleteParam = new GodfsDeleteParam();
        deleteParam.setPath(godfsFilepath);
        var httpResult = godfsHttpClient.post(DELETE_URL, null, deleteParam, null, null);
        if (!httpResult.isSuccess()) {
            log.error("Godfs delete file {} failed: {}", godfsFilepath, httpResult.getResult());
            return false;
        }

        log.info("Godfs delete file {} response: {}", godfsFilepath, httpResult.getResult());
        return true;
    }

    @Data
    public static class GodfsUploadData implements SnakeHttpData {
        private String filename;
        private String output = "json2";
        private InputStreamBody file;

        @Override
        public boolean multipart() {
            return true;
        }
    }

    @Data
    public static class GodfsUploadResData {
        private String domain;
        private String md5;
        private String mtime;
        private String path;
        private String retcode;
        private String retmsg;
        private String scene;
        private String scenes;
        private String size;
        private String src;
        private String url;
    }

    @Data
    public static class GodfsUploadResponse {
        private GodfsUploadResData data;
        private String message;
        private String status;
    }

    @Data
    public static class GodfsDeleteParam implements SnakeHttpParam {
        private String path;
        private String md5;
    }
}
