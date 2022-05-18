package cn.cyberpeace.mp.common.util;

import cn.hutool.core.io.FileUtil;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

/**
 * @Description: 文件后缀校验工具
 * @Author qiaokang
 * @Date：2022/5/18 15:56
 */
public class SuffixCheckUtil {

    /**
     * 根据文件校验后缀
     * @param file 文件
     * @param suffixList 支持后缀集合
     * @return true: 支持，false：不支持
     */
    public static boolean checkByFile(MultipartFile file, List<String> suffixList) {
        if (Objects.isNull(file)) {
            return false;
        }

        // 文件后缀
        String suffix = FileUtil.getSuffix(file.getOriginalFilename());
        if (suffixList.contains(suffix)) {
            return true;
        }

        return false;
    }

    /**
     * 根据文件后缀校验
     * @param suffix 文件后缀
     * @param suffixList 支持后缀集合
     * @return true: 支持，false：不支持
     */
    public static boolean checkBySuffix(String suffix, List<String> suffixList) {
        if (suffixList.contains(suffix)) {
            return true;
        }

        return false;
    }

}
