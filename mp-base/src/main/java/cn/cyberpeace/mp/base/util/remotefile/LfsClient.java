package cn.cyberpeace.mp.base.util.remotefile;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Validator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.util.regex.Pattern;

@Slf4j
public class LfsClient implements FileClient {

    public static final String TMP_DIR_NAME = "lfs";

    public static final String DEFAULT_GROUP = "default";

    public static final Pattern FILE_KEY_PATTERN = Pattern.compile("^[A-Za-z0-9/_\\-\\\\]+(\\.[A-Za-z0-9]+)?$");

    @Getter
    private final String storeDir;

    @Getter
    private final String group;

    @Getter
    private final String groupDir;

    @Getter
    private final Boolean unique;

    private static String genFilenameGroup(String filename) {
        var fileGroupLengthPattern = new int[]{3, 3};
        var filenameLength = filename.length();

        var fileGroups = new String[fileGroupLengthPattern.length];
        var charIndex = 0;
        for (var i = 0; i < fileGroupLengthPattern.length; i++) {
            var fileGroupParts = new String[fileGroupLengthPattern[i]];
            for (var j = 0; j < fileGroupLengthPattern[i]; j++) {
                var realCharIndex = charIndex % filenameLength;
                var key = (int) filename.charAt(realCharIndex);
                fileGroupParts[j] = String.valueOf(key % 10);
                charIndex++;
            }
            fileGroups[i] = StringUtils.join(fileGroupParts);
        }
        return StringUtils.joinWith(FileUtil.FILE_SEPARATOR, (Object[]) fileGroups);
    }

    private static String genFileKey(String filename, String fileSuffix, boolean groupByFilename) {
        if (StringUtils.isEmpty(filename)) {
            filename = TextUtils.uuid();
        }
        if (groupByFilename) {
            filename = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, genFilenameGroup(filename), filename);
        }
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
        this(storeDir, group, true);
    }

    public LfsClient(String storeDir, String group, boolean unique) {
        this.storeDir = FileUtil.getAbsolutePath(storeDir);
        this.group = group;
        this.groupDir = genGroupDir(this.storeDir, this.group);
        this.unique = unique;
    }

    private void validateFileKey(String fileKey) {
        if (!Validator.isMatchRegex(FILE_KEY_PATTERN, fileKey)) {
            throw new MpRuntimeException(String.format("Invalid local file key %s", fileKey));
        }
    }

    public String getTargetFilePath(String fileKey) {
        return StringUtils.joinWith(FileUtil.FILE_SEPARATOR, groupDir, fileKey);
    }

    @Override
    public String upload(InputStream is, long filesize, String fileSuffix) {
        var tmpFileKey = genFileKey(null, fileSuffix, false);
        var tmpFilePath = getTargetFilePath(tmpFileKey);
        try (var os = new FileOutputStream(tmpFilePath)) {
            StreamUtils.copyRange(is, os, 0, filesize);
        } catch (IOException e) {
            var msg = String.format("Local upload with input stream error: %s", e.getMessage());
            log.error(msg);
            if (FileUtil.exist(tmpFilePath)) {
                FileUtil.del(tmpFilePath);
            }
            throw new MpRuntimeException(msg);
        }

        String fileMd5 = null;
        if (Boolean.TRUE.equals(unique)) {
            try (var tis = FileUtil.getInputStream(tmpFilePath)) {
                fileMd5 = DigestUtils.md5Hex(tis);
            } catch (IOException e) {
                var msg = String.format("Local upload with input stream error(get file md5): %s", e.getMessage());
                log.error(msg);
                if (FileUtil.exist(tmpFilePath)) {
                    FileUtil.del(tmpFilePath);
                }
                throw new MpRuntimeException(msg);
            }
        }

        var fileKey = genFileKey(fileMd5, fileSuffix, true);
        var filePath = getTargetFilePath(fileKey);
        if (FileUtil.exist(filePath)) {
            FileUtil.del(tmpFilePath);
        } else {
            FileUtil.move(new File(tmpFilePath), new File(filePath), true);
        }

        return fileKey;
    }

    @Override
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

    @Override
    public boolean delete(String fileKey) {
        validateFileKey(fileKey);

        var targetFilePath = getTargetFilePath(fileKey);
        if (FileUtil.exist(targetFilePath)) {
            FileUtil.del(targetFilePath);
        }
        return true;
    }

}
