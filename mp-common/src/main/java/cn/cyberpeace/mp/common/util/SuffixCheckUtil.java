package cn.cyberpeace.mp.common.util;

import cn.hutool.core.io.FileUtil;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;


public class SuffixCheckUtil {

    
    public static boolean checkByFile(MultipartFile file, List<String> suffixList) {
        if (Objects.isNull(file)) {
            return false;
        }

        
        String suffix = FileUtil.getSuffix(file.getOriginalFilename());
        if (suffixList.contains(suffix)) {
            return true;
        }

        return false;
    }

    
    public static boolean checkBySuffix(String suffix, List<String> suffixList) {
        if (suffixList.contains(suffix)) {
            return true;
        }

        return false;
    }

}
