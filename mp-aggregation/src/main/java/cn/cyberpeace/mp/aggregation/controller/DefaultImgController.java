package cn.cyberpeace.mp.aggregation.controller;

import cn.cyberpeace.mp.aggregation.constant.AggregationSingleton;
import cn.cyberpeace.mp.aggregation.constant.ControllerConstant;
import cn.cyberpeace.mp.aggregation.request.DefaultImgRequest;
import cn.cyberpeace.mp.aggregation.service.DefaultImgService;
import cn.cyberpeace.mp.auth.constant.DefaultImgDirEnum;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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


    /**
     * 获取默认图片
     *
     * @param request
     * @return
     */
    @ApiOperation("获取默认图片")
    @GetMapping("/get-default-img")
    public ServiceResult<List<String>> getDefaultImg(DefaultImgRequest request){
        if (StringUtils.isBlank(AggregationSingleton.aggregationConfig.getStaticLocation())){
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

        fileNameList = convertLocalPaths(fileNameList);

        return ResultCommon.success(fileNameList);
    }

    @ApiOperation("获取系统图片")
    @GetMapping("/get-sys-logo-img")
    public ServiceResult<Map<String, String>> getSysLogoImg(){
        if (StringUtils.isBlank(AggregationSingleton.aggregationConfig.getStaticLocation())){
            throw new MpRuntimeException(BaseErrorCode.DATA_NOT_EXIST);
        }
        List<String> fileNameList = defaultImgService.getFileByDefaultImgDirEnum(DefaultImgDirEnum.SYS_LOGO);
        fileNameList = convertLocalPaths(fileNameList);

        Map<String, String> map = new HashMap<>();
        fileNameList.forEach(fileName -> {
            String name = StringUtils.substring(fileName, StringUtils.lastIndexOf(fileName, "/") + 1);
            map.put(name, fileName);
        });

        return ResultCommon.success(map);
    }

    /**
     * 获取多个指定目录下的默认图片
     * @param defaultImgDirEnums
     * @return
     */
    private List<String> getFileByDefaultImgDirEnums(List<DefaultImgDirEnum> defaultImgDirEnums){
        return defaultImgDirEnums.stream().map(v -> defaultImgService.getFileByDefaultImgDirEnum(v)).flatMap(List::stream).collect(Collectors.toList());
    }

    private List<String> convertLocalPaths(List<String> paths) {
        List<String> convertedPaths =
        paths.stream().map(path -> {
            if (!path.startsWith("/api/mp")) {
                path = "/api/mp" + path;
            }
            return path;
        }).collect(Collectors.toList());

        return convertedPaths;
    }


}
