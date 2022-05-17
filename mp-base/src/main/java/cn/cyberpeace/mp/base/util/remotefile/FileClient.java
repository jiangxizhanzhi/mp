package cn.cyberpeace.mp.base.util.remotefile;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.hutool.core.io.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

public interface FileClient {

    String TMP_DIR_NAME = "tmp_file";

    Logger log = LoggerFactory.getLogger(BaseModelMapper.class);

    default String uploadContent(String content) {
        return uploadContent(content, null);
    }

    default String uploadContent(String content, String fileSuffix) {
        return uploadRawContent(content.getBytes(StandardCharsets.UTF_8), fileSuffix);
    }

    default String uploadRawContent(byte[] content, String fileSuffix) {
        return upload(new ByteArrayInputStream(content), fileSuffix);
    }

    default String upload(String localFilepath) {
        var fileSuffix = FileUtil.getSuffix(localFilepath);
        try (var is = new FileInputStream(localFilepath)) {
            return upload(is, fileSuffix);
        } catch (IOException e) {
            var msg = String.format("File upload get input stream from local file(%s) error: %s", localFilepath,
                    e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
    }

    default String upload(InputStream is) {
        return upload(is, null);
    }

    default String upload(InputStream is, String fileSuffix) {
        long filesize;
        try {
            filesize = is.available();
        } catch (IOException e) {
            var msg = String.format("File upload get available from input stream error: %s", e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
        return upload(is, filesize, fileSuffix);
    }

    String upload(InputStream is, long filesize, String fileSuffix);

    void download(String fileKey, OutputStream os);

    default boolean download(String fileKey, String localFilepath) {
        try (var os = new FileOutputStream(localFilepath)) {
            download(fileKey, os);
        } catch (IOException e) {
            var msg = String.format("File(%s) download get output stream from local path(%s) error: %s", fileKey,
                    localFilepath, e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
        return true;
    }

    default String downloadTmp(String fileKey) {
        return downloadTmp(fileKey, null);
    }

    default String downloadTmp(String fileKey, String filename) {
        var baseTmpDir = FileUtil.getTmpDirPath();
        var tmpDirName = ClassUtils.getStaticFieldValue(this.getClass(), "TMP_DIR_NAME", String.class);
        var tmpFileDir = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, baseTmpDir, tmpDirName);
        if (!FileUtil.exist(tmpFileDir)) {
            FileUtil.mkdir(tmpFileDir);
        }
        if (StringUtils.isEmpty(filename)) {
            filename = FileUtil.getName(fileKey);
        }
        var tmpFilepath = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, tmpFileDir, filename);
        download(fileKey, tmpFilepath);
        return tmpFilepath;
    }

    default String downloadContent(String fileKey) {
        byte[] byteContent = downloadRawContent(fileKey);
        return new String(byteContent, StandardCharsets.UTF_8);
    }

    default byte[] downloadRawContent(String fileKey) {
        byte[] byteContent;
        try (var os = new ByteArrayOutputStream()) {
            download(fileKey, os);
            byteContent = os.toByteArray();
        } catch (IOException e) {
            var msg = String.format("File(%s) download to memory error: %s", fileKey, e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
        return byteContent;
    }

    boolean delete(String fileKey);

}
