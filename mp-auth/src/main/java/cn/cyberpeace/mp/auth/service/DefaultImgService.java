package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.constant.DefaultImgDirEnum;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/3 10:40
 */
@Slf4j
@Service
public class DefaultImgService {


    @Value("${spring.web.resources.static-locations:}")
    private List<String> staticLocations;

    /**
     * 获取单个指定目录下的默认图片
     *
     * @param defaultImgDirEnum
     * @return
     */
    public List<String> getFileByDefaultImgDirEnum(DefaultImgDirEnum defaultImgDirEnum) {
        String endPathName = defaultImgDirEnum.getEndPathName();
        String startWithFile = "file:";
        List<String> pathNameList = staticLocations.stream().map(s -> {
            if (s.startsWith(startWithFile)) {
                s = s.substring(5).trim();
            }
            return s;
        }).filter(StringUtils::isNotBlank).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(pathNameList)) {
            log.error("==========default img config error==================");
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }

        String pathName = pathNameList.get(0).concat(endPathName);

        var logFile = new File(pathName);

        File[] listFiles = logFile.listFiles();

        if (Objects.isNull(listFiles)) {
            log.error("==========file dir not exist:{}==================", pathName);
            throw new MpRuntimeException(BaseErrorCode.DATA_NOT_EXIST);
        }
        List<File> fileList = Arrays.stream(listFiles).filter(File::isFile).sorted(Comparator.comparing(File::getName)).collect(Collectors.toList());
        return fileList.stream().map(f -> endPathName.concat("/").concat(f.getName())).collect(Collectors.toList());
    }

}
