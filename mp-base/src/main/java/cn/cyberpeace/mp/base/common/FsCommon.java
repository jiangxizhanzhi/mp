 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FsCommon.java.
 // File size 13707.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.constant.BaseSingleton;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import cn.cyberpeace.mp.base.util.mptool.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * @ClassName FsCommon
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Component
public class FsCommon {

    private static BaseAppConfig baseAppConfig;

    private static final Pattern FILE_KEY_PATTERN = Pattern.compile("^[A-Za-z0-9/_\\-\\\\]+(\\.[A-Za-z0-9]+)?$");

    private static final String TMP_DIR_NAME = "lfs";
    private static final String DEFAULT_GROUP = "default";
    private static String groupDir;
    private static String storeDir;

    private static Boolean unique;

    /**
     * FsCommon
     *
     * @param baseAppConfig
     */
    @Autowired
    public FsCommon(BaseAppConfig baseAppConfig) {
        FsCommon.baseAppConfig = baseAppConfig;

        FsCommon.storeDir = FileUtil.getAbsolutePath(baseAppConfig.getFileServerDir());
        FsCommon.groupDir = genGroupDir(FsCommon.storeDir, FsCommon.DEFAULT_GROUP);
        FsCommon.unique = true;
    }

    public static String genSpiceUrl(String fileKey) {
        if (StringUtils.isEmpty(fileKey)) {
            return fileKey;
        }
        if (fileKey.startsWith(String.format(FsCommon.baseAppConfig.getLfsOuterSpiceUrlTemplate(), StringUtils.EMPTY))) {
            return fileKey;
        } else {
            return String.format(FsCommon.baseAppConfig.getLfsOuterSpiceUrlTemplate(), fileKey);
        }
    }

    /**
     * genHttpUrl
     *
     * @param fileKey
     * @return
     */
    public static String genHttpUrl(String fileKey) {
        if (StringUtils.isEmpty(fileKey)) {
            return fileKey;
        }
        if (fileKey.startsWith(String.format(FsCommon.baseAppConfig.getLfsOuterHttpUrlTemplate(), StringUtils.EMPTY))) {
            return fileKey;
        } else {
            return String.format(baseAppConfig.getLfsOuterHttpUrlTemplate(), fileKey);
        }
    }

    /**
     * genInnerHttpUrl
     *
     * @param fileKey
     * @return
     */
    public static String genInnerHttpUrl(String fileKey) {
        return String.format(baseAppConfig.getLfsInnerHttpUrlTemplate(), fileKey);
    }

    /**
     * uploadContent
     *
     * @param content
     * @return
     */
    public static String uploadContent(String content) {
        return uploadContent(content, null);
    }

    /**
     * uploadContent
     *
     * @param content
     * @param fileSuffix
     * @return
     */
    public static String uploadContent(String content, String fileSuffix) {
        return uploadRawContent(content.getBytes(StandardCharsets.UTF_8), fileSuffix);
    }

    /**
     * uploadRawContent
     *
     * @param content
     * @param fileSuffix
     * @return
     */
    public static String uploadRawContent(byte[] content, String fileSuffix) {
        return upload(new ByteArrayInputStream(content), fileSuffix);
    }

    /**
     * upload
     *
     * @param localFilepath
     * @return
     */
    public static String upload(String localFilepath) {
        String fileSuffix = FileUtil.getSuffix(localFilepath);
        try (InputStream is = new FileInputStream(localFilepath)) {
            return upload(is, fileSuffix);
        } catch (IOException e) {
            String msg = String.format("File upload get input stream from local file(%s) error: %s", localFilepath,
                    e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
    }

    /**
     * upload
     *
     * @param is
     * @return
     */
    public static String upload(InputStream is) {
        return upload(is, null);
    }

    /**
     * upload
     *
     * @param is
     * @param fileSuffix
     * @return
     */
    public static String upload(InputStream is, String fileSuffix) {
        long filesize;
        try {
            filesize = is.available();
        } catch (IOException e) {
            String msg = String.format("File upload get available from input stream error: %s", e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
        return upload(is, filesize, fileSuffix);
    }

    /**
     * upload
     *
     * @param is
     * @param filesize
     * @param fileSuffix
     * @return
     */
    public static String upload(InputStream is, long filesize, String fileSuffix) {
        log.info("filesize:{}, fileSuffix:{}", filesize, fileSuffix);
        String tmpFileKey = genFileKey(null, fileSuffix, false);
        log.info("tmpFileKey:{}", tmpFileKey);
        String tmpFilePath = getTargetFilePath(tmpFileKey);
        log.info("tmpFilePath:{}", tmpFilePath);
        try (OutputStream os = new FileOutputStream(tmpFilePath)) {
            StreamUtils.copyRange(is, os, 0, filesize);
        } catch (IOException e) {
            String msg = String.format("Local upload with input stream error: %s", e.getMessage());
            log.error(msg);
            if (FileUtil.exist(tmpFilePath)) {
                FileUtil.del(tmpFilePath);
            }
            throw new MpRuntimeException(msg);
        }

        String fileMd5 = null;
        if (Boolean.TRUE.equals(unique)) {
            try (InputStream tis = FileUtil.getInputStream(tmpFilePath)) {
                fileMd5 = DigestUtils.md5Hex(tis);
            } catch (IOException e) {
                String msg = String.format("Local upload with input stream error(get file md5): %s", e.getMessage());
                log.error(msg);
                if (FileUtil.exist(tmpFilePath)) {
                    FileUtil.del(tmpFilePath);
                }
                throw new MpRuntimeException(msg);
            }
        }

        String fileKey = genFileKey(fileMd5, fileSuffix, true);
        log.info("fileKey:{}", fileKey);
        String filePath = getTargetFilePath(fileKey);
        log.info("filePath:{}", filePath);
        log.info("filePath.getParent:{}", new File(filePath).getParent());
        if (FileUtil.exist(filePath)) {
            FileUtil.del(tmpFilePath);
        } else {
            FileUtil.move(new File(tmpFilePath), new File(filePath), true);
        }

        return fileKey;
    }

    /**
     * download
     *
     * @param fileKey
     * @param os
     */
    public static void download(String fileKey, OutputStream os) {
        validateFileKey(fileKey);

        String targetFilePath = getTargetFilePath(fileKey);
        if (!FileUtil.exist(targetFilePath)) {
            String msg = String.format("Local download %s to output stream error: file not found", fileKey);
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
        try (InputStream is = new FileInputStream(targetFilePath)) {
            StreamUtils.copy(is, os);
        } catch (IOException e) {
            String msg = String.format("Local download %s to output stream error: %s", fileKey, e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
    }

    /**
     * download
     *
     * @param fileKey
     * @param localFilepath
     * @return boolean
     */
    public static boolean download(String fileKey, String localFilepath) {
        try (OutputStream os = new FileOutputStream(localFilepath)) {
            download(fileKey, os);
        } catch (IOException e) {
            String msg = String.format("File(%s) download get output stream from local path(%s) error: %s", fileKey,
                    localFilepath, e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
        return true;
    }

    /**
     * downloadTmp
     *
     * @param fileKey
     * @return
     */
    public static String downloadTmp(String fileKey) {
        return downloadTmp(fileKey, null);
    }

    /**
     * downloadTmp
     *
     * @param fileKey
     * @param filename
     * @return
     */
    public static String downloadTmp(String fileKey, String filename) {
        String baseTmpDir = FileUtil.getTmpDirPath();
        String tmpFileDir = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, baseTmpDir, TMP_DIR_NAME);
        if (!FileUtil.exist(tmpFileDir)) {
            final File mkdir = FileUtil.mkdir(tmpFileDir);
        }
        if (StringUtils.isEmpty(filename)) {
            filename = FileUtil.getName(fileKey);
        }
        String tmpFilepath = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, tmpFileDir, filename);
        final boolean downloadBoolean = download(fileKey, tmpFilepath);
        return tmpFilepath;
    }

    /**
     * downloadContent
     *
     * @param fileKey
     * @return
     */
    public static String downloadContent(String fileKey) {
        byte[] byteContent = downloadRawContent(fileKey);
        return new String(byteContent, StandardCharsets.UTF_8);
    }

    /**
     * downloadRawContent
     *
     * @param fileKey
     * @return
     */
    public static byte[] downloadRawContent(String fileKey) {
        byte[] byteContent;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            download(fileKey, os);
            byteContent = os.toByteArray();
        } catch (IOException e) {
            String msg = String.format("File(%s) download to memory error: %s", fileKey, e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
        return byteContent;
    }

    /**
     * delete
     *
     * @param fileKey
     * @return boolean
     */
    public static boolean delete(String fileKey) {
        validateFileKey(fileKey);

        String targetFilePath = getTargetFilePath(fileKey);
        if (FileUtil.exist(targetFilePath)) {
            FileUtil.del(targetFilePath);
        }
        return true;
    }

    /**
     * isFileServerOuterUrl
     *
     * @param path
     * @return boolean
     */
    public static boolean isFileServerOuterUrl(String path) {
        String prefix = String.format(BaseSingleton.getBaseAppConfig().getLfsOuterHttpUrlTemplate(), StringUtils.EMPTY);
        return StringUtils.startsWith(path, prefix);
    }

    /**
     * getFileKey
     *
     * @param path
     * @return
     */
    public static String getFileKey(String path) {
        if (StringUtils.isEmpty(path)) {
            return path;
        }
        String prefix = String.format(BaseSingleton.getBaseAppConfig().getLfsOuterHttpUrlTemplate(), StringUtils.EMPTY);
        return StringUtils.replaceOnce(path, prefix, StringUtils.EMPTY);
    }

    /**
     * getGroupDir
     *
     * @return
     */
    public static String getGroupDir() {
        return FsCommon.groupDir;
    }

    /**
     * getTargetFilePath
     *
     * @param fileKey
     * @return
     */
    private static String getTargetFilePath(String fileKey) {
        return StringUtils.joinWith(FileUtil.FILE_SEPARATOR, groupDir, fileKey);
    }

    /**
     * genFileKey
     *
     * @param filename
     * @param fileSuffix
     * @param groupByFilename
     * @return
     */
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

    /**
     * genFilenameGroup
     *
     * @param filename
     * @return
     */
    private static String genFilenameGroup(String filename) {
        int[] fileGroupLengthPattern = new int[]{3, 3};
        int filenameLength = filename.length();

        String[] fileGroups = new String[fileGroupLengthPattern.length];
        int charIndex = 0;
        for (int i = 0; i < fileGroupLengthPattern.length; i++) {
            String[] fileGroupParts = new String[fileGroupLengthPattern[i]];
            for (int j = 0; j < fileGroupLengthPattern[i]; j++) {
                int realCharIndex = charIndex % filenameLength;
                int key = (int) filename.charAt(realCharIndex);
                fileGroupParts[j] = String.valueOf(key % 10);
                charIndex++;
            }
            fileGroups[i] = StringUtils.join(fileGroupParts);
        }
        return StringUtils.joinWith(FileUtil.FILE_SEPARATOR, (Object[]) fileGroups);
    }

    /**
     * genGroupDir
     *
     * @param storeDir
     * @param group
     * @return
     */
    private static String genGroupDir(String storeDir, String group) {
        String groupDir = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, storeDir, group);
        final File mkdir = FileUtil.mkdir(groupDir);
        return groupDir;
    }

    /**
     * validateFileKey
     *
     * @param fileKey
     */
    private static void validateFileKey(String fileKey) {
        if (!Validator.isMatchRegex(FILE_KEY_PATTERN, fileKey)) {
            throw new MpRuntimeException(String.format("Invalid local file key %s", fileKey));
        }
    }
}
