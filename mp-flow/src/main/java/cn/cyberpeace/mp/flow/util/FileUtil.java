package cn.cyberpeace.mp.flow.util;

import cn.cyberpeace.mp.flow.constant.BaseConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * @Description: 文件工具类
 * @Author qiaokang
 * @Date：2021/8/31 11:13
 */
@Component
@Slf4j
public class FileUtil {

    /**
     * 获取文件后缀
     * @param filename
     * @return
     */
    public static String getSuffix(String filename) {
        String suffix = StringUtils.EMPTY;
        if (StringUtils.isBlank(filename)) {
            return suffix;
        }

        Path path = Path.of(filename);
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf(BaseConstants.SEPARATOR_CHAR_DOT);
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            suffix = fileName.substring(dotIndex + 1);
        }

        return suffix;
    }

}