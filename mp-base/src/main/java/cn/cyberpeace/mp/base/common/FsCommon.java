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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

//        FsCommon.storeDir = FileUtil.getAbsolutePath(baseAppConfig.getFileServerDir());
//        FsCommon.groupDir = genGroupDir(FsCommon.storeDir, FsCommon.DEFAULT_GROUP);
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
        String fileSuffix = "";
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
        return fileSuffix;
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


}
