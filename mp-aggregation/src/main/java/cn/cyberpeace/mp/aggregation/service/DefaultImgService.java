package cn.cyberpeace.mp.aggregation.service;

import cn.cyberpeace.mp.aggregation.constant.AggregationSingleton;
import cn.cyberpeace.mp.auth.constant.DefaultImgDirEnum;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
public class DefaultImgService {

    
    public List<String> getFileByDefaultImgDirEnum(DefaultImgDirEnum defaultImgDirEnum) {
        String endPathName = defaultImgDirEnum.getEndPathName();
        String startWithFile = "file:";
        String pathName = AggregationSingleton.aggregationConfig.getStaticLocation();
        if (StringUtils.isBlank(pathName)) {
            log.error("default img config error, pathName[{}] is blank", pathName);
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }

        if (pathName.startsWith(startWithFile)) {
            pathName = pathName.substring(5).trim();
        }
        pathName = pathName.concat(endPathName);

        var logFile = new File(pathName);

        File[] listFiles = logFile.listFiles();

        if (Objects.isNull(listFiles)) {
            log.error("==========file dir not exist:{}==================", pathName);
            throw new MpRuntimeException(BaseErrorCode.DATA_NOT_EXIST);
        }
        List<File> fileList = Arrays.stream(listFiles).filter(File::isFile).sorted(Comparator.comparing(File::getName)).collect(Collectors.toList());
        return fileList.stream().map(f -> endPathName.concat("/").concat(f.getName())).collect(Collectors.toList());
    }

    public String getDefaultImgFilePath(String relativePath) {
        String startWithFile = "file:";
        String pathName = AggregationSingleton.aggregationConfig.getStaticLocation();
        if (StringUtils.isBlank(pathName)) {
            log.error("default img config error, pathName is blank");
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }

        if (pathName.startsWith(startWithFile)) {
            pathName = pathName.substring(5).trim();
        }
        if (StringUtils.isNotBlank(pathName)) {
            String filePath = StringUtils.joinWith("", pathName, relativePath);
            File file = new File(filePath);
            if (file.exists()) {
                return filePath;
            }
        }
        return null;
    }

}
