package cn.cyberpeace.mp.base.util.remotefile;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Validator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Slf4j
public class LfsClient implements FileClient {

    public static final String DEFAULT_GROUP = "default";

    public static final Pattern FILE_KEY_PATTERN = Pattern.compile("^[A-Za-z0-9]+(\\.[A-Za-z0-9]+)?$");

    @Getter
    private final String storeDir;

    @Getter
    private final String group;

    @Getter
    private final String groupDir;

    private static String genFileKey(String fileSuffix) {
        var filename = TextUtils.uuid();
        if (!StringUtils.isEmpty(fileSuffix)) {
            filename = StringUtils.joinWith(".", filename, fileSuffix);
        }
        return filename;
    }

    private static String genGroupDir(String storeDir, String group) {
        var groupDir = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, storeDir, group);
        FileUtil.mkdir(groupDir);
        return groupDir;
    }

    public LfsClient(String storeDir) {
        this(storeDir, DEFAULT_GROUP);
    }

    public LfsClient(String storeDir, String group) {
        this.storeDir = FileUtil.getAbsolutePath(storeDir);
        this.group = group;
        this.groupDir = genGroupDir(this.storeDir, this.group);
    }

    private void validateFileKey(String fileKey) {
        if (!Validator.isMatchRegex(FILE_KEY_PATTERN, fileKey)) {
            throw new MpRuntimeException(String.format("Invalid local file key %s", fileKey));
        }
    }

    private String getTargetFilePath(String fileKey) {
        return StringUtils.joinWith(FileUtil.FILE_SEPARATOR, groupDir, fileKey);
    }

    public String upload(String localFilepath) {
        var fileSuffix = FileUtil.getSuffix(localFilepath);
        var fileKey = genFileKey(fileSuffix);
        var targetFilePath = getTargetFilePath(fileKey);
        FileUtil.copyFile(localFilepath, targetFilePath);
        return fileKey;
    }

    public String upload(InputStream is, long filesize, String fileSuffix) {
        var fileKey = genFileKey(fileSuffix);
        var targetFilePath = getTargetFilePath(fileKey);
        try (var os = new FileOutputStream(targetFilePath)) {
            StreamUtils.copyRange(is, os, 0, filesize);
        } catch (IOException e) {
            var msg = String.format("Local upload with input stream error: %s", e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
        return fileKey;
    }

    public void download(String fileKey, OutputStream os) {
        validateFileKey(fileKey);

        var targetFilePath = getTargetFilePath(fileKey);
        if (!FileUtil.exist(targetFilePath)) {
            var msg = String.format("Local download %s to output stream error: file not found", fileKey);
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
        try (var is = new FileInputStream(targetFilePath)) {
            StreamUtils.copy(is, os);
        } catch (IOException e) {
            var msg = String.format("Local download %s to output stream error: %s", fileKey, e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
    }

    public boolean download(String fileKey, String localFilepath) {
        validateFileKey(fileKey);

        var targetFilePath = getTargetFilePath(fileKey);
        if (!FileUtil.exist(targetFilePath)) {
            var msg = String.format("Local download %s to %s error: file not found", fileKey, localFilepath);
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
        FileUtil.copyFile(targetFilePath, localFilepath);
        return true;
    }

    public String downloadContent(String fileKey) {
        validateFileKey(fileKey);

        var targetFilePath = getTargetFilePath(fileKey);
        if (!FileUtil.exist(targetFilePath)) {
            var msg = String.format("Local download %s content error: file not found", fileKey);
            log.error(msg);
            throw new MpRuntimeException(msg);
        }

        byte[] byteContent;
        try (var is = new FileInputStream(targetFilePath)) {
            byteContent = is.readAllBytes();
        } catch (IOException e) {
            var msg = String.format("Local download %s to content error: %s", fileKey, e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
        return new String(byteContent, StandardCharsets.UTF_8);
    }

    public boolean delete(String fileKey) {
        validateFileKey(fileKey);

        var targetFilePath = getTargetFilePath(fileKey);
        if (FileUtil.exist(targetFilePath)) {
            FileUtil.del(targetFilePath);
        }
        return true;
    }

}
