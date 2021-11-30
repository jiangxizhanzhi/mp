package cn.cyberpeace.mp.aggregation.controller;

import cn.cyberpeace.mp.aggregation.constant.ControllerConstant;
import cn.cyberpeace.mp.aggregation.request.DefaultImgRequest;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
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

    @ApiOperation("获取默认图片")
    @GetMapping("/get-default-img")
    public ServiceResult<List<String>> getDefaultImg(DefaultImgRequest request){
        if (CollectionUtils.isEmpty(staticLocations)){
            throw new MpRuntimeException(BaseErrorCode.DATA_NOT_EXIST);
        }

        String endPathName = request.getDefaultImgDirEnum().getEndPathName();

        String startWithFile = "file:";
        List<String> pathNameList = staticLocations.stream().map(s -> {
            if (s.startsWith(startWithFile)){
                s = s.substring(5).trim();
            }
            return s;
        }).filter(StringUtils::isNotBlank).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(pathNameList)){
            throw new MpRuntimeException(BaseErrorCode.DATA_NOT_EXIST);
        }
        var logFile = new File(pathNameList.get(0).concat(endPathName));
        List<File> fileList = Arrays.stream(Objects.requireNonNull(logFile.listFiles())).filter(File::isFile).collect(Collectors.toList());
        List<String> fileNameList = fileList.stream().map(f -> endPathName.concat("/").concat(f.getName())).collect(Collectors.toList());
        return new ServiceResult<>(fileNameList).asResult();
    }
}
