package cn.cyberpeace.mp.devops.cluster.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

public class FileUtils {

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
            prefix = fileName.substring(fileName.lastIndexOf("."));
            // 用uuid作为文件名，防止生成的临时文件重复
            final File file = File.createTempFile(UUID.randomUUID().toString(), prefix);
            // MultipartFile to File
            multipartFile.transferTo(file);
            fileList.add(file);
        }
        return fileList;
    }


    /**
     * 转换 MultipartFile to File,生成临时文件，原文件名称修改为uuid
     * @param multipartFiles
     * @return
     * @throws IOException
     */
    public static Map<String,File> transferToFileMap(List<MultipartFile> multipartFiles) throws IOException {
        if (CollectionUtils.isEmpty(multipartFiles)){
            return new HashMap<>();
        }
        Map<String,File> fileList = new HashMap<>();
        String fileName;
        String prefix;
        for (MultipartFile multipartFile : multipartFiles) {
            // 获取文件名
            fileName = multipartFile.getOriginalFilename();
            // 获取文件后缀
            prefix = fileName.substring(fileName.lastIndexOf("."));
            // 用uuid作为文件名，防止生成的临时文件重复
            final File file = File.createTempFile(UUID.randomUUID().toString(), prefix);
            // MultipartFile to File
            multipartFile.transferTo(file);
            fileList.put(fileName,file);
        }
        return fileList;
    }

    /**
     * 多个MultipartFile转File,不生成临时文件
     * @param multipartFiles
     * @return
     * @throws Exception
     */
    public static Map<String,File> transferToRealFileMap(List<MultipartFile> multipartFiles) throws Exception {
        if (CollectionUtils.isEmpty(multipartFiles)){
            return new HashMap<>();
        }

        Map<String,File> fileList = new HashMap<>();
        String fileName;
        String prefix;
        for (MultipartFile multipartFile : multipartFiles) {
            File file = multipartFileToFile(multipartFile);
            fileName = multipartFile.getOriginalFilename();
            fileList.put(fileName,file);
        }
        return fileList;
    }

    /**
     * MultipartFile转File
     * @param file
     * @return
     * @throws Exception
     */
    public static File multipartFileToFile(MultipartFile file) throws Exception {

        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;
    }

    /**
     * InputStream流转File
     * @param ins
     * @param file
     */
    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 1024)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
