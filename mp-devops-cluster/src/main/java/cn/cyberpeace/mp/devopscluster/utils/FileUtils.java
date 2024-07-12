 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FileUtils.java.
 // File size 3450.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.utils;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName FileUtils
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class FileUtils {

    /**
     * FileUtils
     */
    private FileUtils(){
        throw new IllegalStateException("Utility class");
    }

    /**
     * 转换 MultipartFile to File
     * @param multipartFiles
     * @return
     * @throws IOException
     */
    public static List<File> transferToFile(List<MultipartFile> multipartFiles) throws IOException {
        if (CollectionUtils.isEmpty(multipartFiles)){
            return new ArrayList<>();
        }
        List<File> fileList = new ArrayList<>();
        String fileName;
        String prefix;
        for (MultipartFile multipartFile : multipartFiles) {
            // 获取文件名
            fileName = multipartFile.getOriginalFilename();
            // 获取文件后缀
            prefix = StringUtils.isBlank(fileName) ? "" : fileName.substring(fileName.lastIndexOf("."));
            // 用uuid作为文件名，防止生成的临时文件重复
            final File file = File.createTempFile(UUID.randomUUID().toString(), prefix);
            // MultipartFile to File
            multipartFile.transferTo(file);
            fileList.add(file);
        }
        return fileList;
    }

    /**
     * transferToFileList
     *
     * @param multipartFiles
     * @return
     */
    public static List<File> transferToFileList(List<MultipartFile> multipartFiles) throws IOException {
        if (CollectionUtils.isEmpty(multipartFiles)){
            return Lists.newArrayList();
        }
        List<File> fileList = Lists.newArrayList();
        String originalFilename;
        String prefix;
        final File sysTempDir = new File(System.getProperty("java.io.tmpdir"));

        for (MultipartFile multipartFile : multipartFiles) {
            // 获取文件名
            originalFilename = multipartFile.getOriginalFilename();
            int index = StringUtils.isBlank(originalFilename) ? -1 : originalFilename.lastIndexOf(".");
            // 获取文件后缀
            if (index < 0 ){
                prefix = ".img";
            }else{
                prefix = originalFilename.substring(index);
            }
            String dirName = UUID.randomUUID().toString();
            File newTempDir = new File(sysTempDir, dirName);
            if (!newTempDir.exists()){
                newTempDir.mkdirs();
            }
            if (index < 0) {
                final File file = new File(newTempDir, originalFilename);
                // MultipartFile to File
                multipartFile.transferTo(file);
                fileList.add(file);
            } else {
                final File file = new File(newTempDir, originalFilename.substring(0, index).concat(prefix));
                // MultipartFile to File
                multipartFile.transferTo(file);
                fileList.add(file);
            }
        }
        return fileList;
    }
}
