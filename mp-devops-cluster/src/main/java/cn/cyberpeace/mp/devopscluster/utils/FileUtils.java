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

public class FileUtils {

    private FileUtils(){
        throw new IllegalStateException("Utility class");
    }

    
    public static List<File> transferToFile(List<MultipartFile> multipartFiles) throws IOException {
        if (CollectionUtils.isEmpty(multipartFiles)){
            return new ArrayList<>();
        }
        List<File> fileList = new ArrayList<>();
        String fileName;
        String prefix;
        for (MultipartFile multipartFile : multipartFiles) {
            
            fileName = multipartFile.getOriginalFilename();
            
            prefix = StringUtils.isBlank(fileName) ? "" : fileName.substring(fileName.lastIndexOf("."));
            
            final File file = File.createTempFile(UUID.randomUUID().toString(), prefix);
            
            multipartFile.transferTo(file);
            fileList.add(file);
        }
        return fileList;
    }

    public static List<File> transferToFileList(List<MultipartFile> multipartFiles) throws IOException {
        if (CollectionUtils.isEmpty(multipartFiles)){
            return Lists.newArrayList();
        }
        List<File> fileList = Lists.newArrayList();
        String originalFilename;
        String prefix;
        final File sysTempDir = new File(System.getProperty("java.io.tmpdir"));

        for (MultipartFile multipartFile : multipartFiles) {
            
            originalFilename = multipartFile.getOriginalFilename();
            int index = StringUtils.isBlank(originalFilename) ? -1 : originalFilename.lastIndexOf(".");
            
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
            
            multipartFile.transferTo(file);
            fileList.add(file);
        }
        return fileList;
    }
}
