package cn.cyberpeace.mp.aggregation.controller;

import cn.cyberpeace.mp.aggregation.constant.ControllerConstant;
import cn.cyberpeace.mp.aggregation.request.DefaultImgRequest;
import cn.cyberpeace.mp.auth.constant.DefaultImgDirEnum;
import cn.cyberpeace.mp.auth.service.DefaultImgService;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wanggang
 */
@Api(value = "默认图片接口", tags = "默认图片接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/default-img")
public class DefaultImgController {

    @Autowired
    private DefaultImgService defaultImgService;

    @Value("${spring.web.resources.static-locations:}")
    private List<String> staticLocations;


    /**
     * 获取默认图片
     *
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
                    DefaultImgDirEnum.SAFETY_EQUIPMENT, DefaultImgDirEnum.COMMON_TARGET);
            fileNameList = getFileByDefaultImgDirEnums(defaultImgDirEnums);
        }else{
            fileNameList = defaultImgService.getFileByDefaultImgDirEnum(defaultImgDirEnum);
        }
        return new ServiceResult<>(fileNameList).asResult();
    }

    /**
     * 获取多个指定目录下的默认图片
     * @param defaultImgDirEnums
     * @return
     */
    private List<String> getFileByDefaultImgDirEnums(List<DefaultImgDirEnum> defaultImgDirEnums){
        return defaultImgDirEnums.stream().map(v -> defaultImgService.getFileByDefaultImgDirEnum(v)).flatMap(List::stream).collect(Collectors.toList());
    }


}
