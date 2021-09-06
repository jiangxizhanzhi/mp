package cn.cyberpeace.mp.devops.cluster.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUtils {

    /**
     * 转换 MultipartFile to File
     * @param multipartFiles
     * @return
     * @throws IOException
     */
    public static List<File> transferToFile(List<MultipartFile> multipartFiles) throws IOException {
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
}
