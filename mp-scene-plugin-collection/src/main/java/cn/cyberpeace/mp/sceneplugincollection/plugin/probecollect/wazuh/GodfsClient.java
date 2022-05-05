package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh;

import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpClient;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpData;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpParam;
import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;

@Slf4j
public class GodfsClient {

    public static final String STATUS_OK = "ok";

    public static final String UPLOAD_URL = "/godfs/upload";
    public static final String DELETE_URL = "/group1/delete";
    public static final String DOWNLOAD_URL_PREFIX = "/godfs";

    @Getter
    private HttpClient godfsHttpClient;

    public GodfsClient(String godfsServer) {
        this.godfsHttpClient = new HttpClient(godfsServer, 600000, 200, 200);
        this.godfsHttpClient.setLogRequest(true);
    }

    public String upload(String localFilepath) {
        return upload(localFilepath, null);
    }

    public String upload(String localFilepath, String filename) {
        if (!FileUtil.exist(localFilepath)) {
            log.error("upload file {} to godfs failed: file not exist", localFilepath);
            return null;
        }

        if (StringUtils.isEmpty(filename)) {
            var fileSuffix  = FileUtil.getSuffix(localFilepath);
            String fileMd5Value;
            try {
                fileMd5Value = DigestUtils.md5Hex(FileUtil.getInputStream(localFilepath));
            } catch (IOException e) {
                log.error("upload file {} to godfs failed: get file md5 value error - {}", localFilepath, e);
                return null;
            }
            filename = StringUtils.joinWith(".", fileMd5Value, fileSuffix);
        }

        var uploadData = new GodfsUploadData();
        uploadData.setFilename(filename);
        uploadData.setFile(FileUtil.file(localFilepath));
        var httpResult = godfsHttpClient.post(UPLOAD_URL, uploadData, null, null, null);
        if (!httpResult.isSuccess()) {
            log.error("upload file {} to godfs with filename {} error: {}", localFilepath, filename,
                    httpResult.getResult());
            return null;
        }

        GodfsUploadResponse response = httpResult.snakeGetResultBean(new TypeReference<GodfsUploadResponse>() {});
        if (response == null) {
            log.error("upload file {} to godfs with filename {} parse result({}) failed", localFilepath, filename,
                    httpResult.getResult());
            return null;
        }

        if (!STATUS_OK.equals(response.getStatus())) {
            log.error("upload file {} to godfs with filename {} error: {}, result({})", localFilepath, filename,
                    response.getMessage(), httpResult.getResult());
            return null;
        }

        return response.getData().getPath();
    }

    public boolean download(String path, String localFilepath) {
        var downloadUrl = StringUtils.join(DOWNLOAD_URL_PREFIX, path);
        var httpResult = godfsHttpClient.resGet(downloadUrl, null, null, null);
        if (!httpResult.isSuccess()) {
            log.error("download godfs file {} error: {}", path, httpResult.getResult());
            return false;
        }

        var entity = httpResult.getResponse().getEntity();
        try {
            FileCopyUtils.copy(entity.getContent(), FileUtil.getOutputStream(localFilepath));
        } catch (IOException e) {
            log.error("download godfs file {} error: get content error - {}", path, e);
            return false;
        }

        return true;
    }

    public boolean delete(String path) {
        var deleteParam = new GodfsDeleteParam();
        deleteParam.setPath(path);
        var httpResult = godfsHttpClient.post(DELETE_URL, null, deleteParam, null, null);
        if (!httpResult.isSuccess()) {
            log.error("delete godfs file {} failed: {}", path, httpResult.getResult());
            return false;
        }

        log.info("delete godfs file {} response: {}", path, httpResult.getResult());
        return true;
    }

    @Data
    public static class GodfsUploadData implements HttpData {
        private String filename;
        private String output = "json2";
        private File file;

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
    public static class GodfsDeleteParam implements HttpParam {
        private String path;
        private String md5;
    }

}
