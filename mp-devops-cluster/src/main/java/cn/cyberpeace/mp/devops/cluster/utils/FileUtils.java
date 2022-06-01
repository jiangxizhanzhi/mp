package cn.cyberpeace.mp.devops.cluster.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FileUtils {

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
     *
     * @param multipartFiles
     * @return
     * @throws IOException
     */
    public static Map<String,File> transferToFileMap(List<MultipartFile> multipartFiles) throws IOException {
        if (CollectionUtils.isEmpty(multipartFiles)){
            return new HashMap<>();
        }
        Map<String,File> fileList = new HashMap<>();
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
            final File file = new File(newTempDir,originalFilename.substring(0,index).concat(prefix));
            // MultipartFile to File
            multipartFile.transferTo(file);
            file.deleteOnExit();
            fileList.put(originalFilename,file);
        }
        return fileList;
    }
}
