package cn.cyberpeace.mp.base.util.remotefile;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.hutool.core.io.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public interface FileClient {

    String TMP_DIR_NAME = "tmp_file";

    Logger log = LoggerFactory.getLogger(BaseModelMapper.class);

    String upload(String localFilepath);

    default String uploadContent(String content) {
        return uploadContent(content, null);
    }

    default String uploadContent(String content, String fileSuffix) {
        return upload(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), fileSuffix);
    }

    default String upload(InputStream is) {
        return upload(is, null);
    }

    default String upload(InputStream is, String fileSuffix) {
        long filesize;
        try {
            filesize = is.available();
        } catch (IOException e) {
            e.printStackTrace();
            var msg = String.format("File upload get available from input stream error: %s", e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
        return upload(is, filesize, fileSuffix);
    }

    String upload(InputStream is, long filesize, String fileSuffix);

    void download(String fileKey, OutputStream os);

    boolean download(String fileKey, String localFilepath);

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

    String downloadContent(String fileKey);

    boolean delete(String fileKey);

}
