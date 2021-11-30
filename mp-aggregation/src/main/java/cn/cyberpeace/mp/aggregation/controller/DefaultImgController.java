package cn.cyberpeace.mp.aggregation.controller;

import cn.cyberpeace.mp.aggregation.constant.ControllerConstant;
import cn.cyberpeace.mp.aggregation.request.DefaultImgRequest;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.situationaware.model.enums.DefaultImgDirEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wanggang
 */
@Api(value = "默认图片接口", tags = "默认图片接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/default-img")
public class DefaultImgController {

    @Value("${spring.web.resources.static-locations:}")
    private List<String> staticLocations;

    /**
     * 获取默认图片
     * @param request
     * @return
     */
    @ApiOperation("获取默认图片")
    @GetMapping("/get-default-img")
    public ServiceResult<List<String>> getDefaultImg(DefaultImgRequest request){
        if (CollectionUtils.isEmpty(staticLocations)){
            throw new MpRuntimeException(BaseErrorCode.DATA_NOT_EXIST);
        }
        DefaultImgDirEnum defaultImgDirEnum = request.getDefaultImgDirEnum();
        List<String> fileNameList;
        if (defaultImgDirEnum == DefaultImgDirEnum.TARGET){
            List<DefaultImgDirEnum> defaultImgDirEnums = Arrays.asList(DefaultImgDirEnum.APPLICATION_SYSTEM,
                    DefaultImgDirEnum.NETWORK_EQUIPMENT,DefaultImgDirEnum.OPERATION_SYSTEM,
                    DefaultImgDirEnum.SAFETY_EQUIPMENT,DefaultImgDirEnum.STATUS);
            fileNameList = getFileByDefaultImgDirEnums(defaultImgDirEnums);
        }else{
            fileNameList = getFileByDefaultImgDirEnum(defaultImgDirEnum);
        }
        return new ServiceResult<>(fileNameList).asResult();
    }

    /**
     * 获取多个指定目录下的默认图片
     * @param defaultImgDirEnums
     * @return
     */
    private List<String> getFileByDefaultImgDirEnums(List<DefaultImgDirEnum> defaultImgDirEnums){
        return defaultImgDirEnums.stream().map(this::getFileByDefaultImgDirEnum).flatMap(List::stream).collect(Collectors.toList());
    }

    /**
     * 获取单个指定目录下的默认图片
     * @param defaultImgDirEnum
     * @return
     */
    private List<String> getFileByDefaultImgDirEnum(DefaultImgDirEnum defaultImgDirEnum){
        String endPathName = defaultImgDirEnum.getEndPathName();
        String startWithFile = "file:";
        List<String> pathNameList = staticLocations.stream().map(s -> {
            if (s.startsWith(startWithFile)){
                s = s.substring(5).trim();
            }
            return s;
        }).filter(StringUtils::isNotBlank).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(pathNameList)){
            log.error("==========default img config error==================");
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }

        String pathName = pathNameList.get(0).concat(endPathName);

        var logFile = new File(pathName);

        File[] listFiles = logFile.listFiles();

        if (Objects.isNull(listFiles)){
            log.error("==========file dir not exist:{}==================",pathName);
            throw new MpRuntimeException(BaseErrorCode.DATA_NOT_EXIST);
        }
        List<File> fileList = Arrays.stream(listFiles).filter(File::isFile).sorted(Comparator.comparing(File::getName)).collect(Collectors.toList());
        return fileList.stream().map(f -> endPathName.concat("/").concat(f.getName())).collect(Collectors.toList());
    }
}
