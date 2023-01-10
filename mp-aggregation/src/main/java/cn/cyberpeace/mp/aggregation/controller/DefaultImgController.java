package cn.cyberpeace.mp.aggregation.controller;

import cn.cyberpeace.mp.aggregation.constant.AggregationSingleton;
import cn.cyberpeace.mp.aggregation.constant.ControllerConstant;
import cn.cyberpeace.mp.aggregation.request.DefaultImgRequest;
import cn.cyberpeace.mp.aggregation.service.DefaultImgService;
import cn.cyberpeace.mp.auth.constant.DefaultImgDirEnum;
import cn.cyberpeace.mp.auth.util.DefaultImgUtil;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
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

    public static final String DEFAULT_IMG_PATH = "/root/.mp/resources/default_img";


    /**
     * 获取默认图片
     *
     * @param request
     * @return
     */
    @ApiOperation("获取默认图片")
    @GetMapping("/get-default-img")
    public ServiceResult<List<String>> getDefaultImg(DefaultImgRequest request) {
        if (StringUtils.isBlank(AggregationSingleton.aggregationConfig.getStaticLocation())) {
            throw new MpRuntimeException(BaseErrorCode.DATA_NOT_EXIST);
        }
        DefaultImgDirEnum defaultImgDirEnum = request.getDefaultImgDirEnum();
        /*
        List<String> groupList = Collections.singletonList(defaultImgDirEnum.getGroup());
        List<LocalFileMappingModel> modelList = DefaultImgUtil.listByGroups(groupList);
        List<String> urlList = modelList.stream().map(model ->
           "/api/mp" + model.getLocalFile().substring(DEFAULT_IMG_PATH.length())).collect(Collectors.toList()
        );
        return ResultCommon.success(urlList);
        */

        List<String> fileNameList;
        if (defaultImgDirEnum == DefaultImgDirEnum.TARGET){
            List<DefaultImgDirEnum> defaultImgDirEnums = Arrays.asList(DefaultImgDirEnum.APPLICATION_SYSTEM,
                    DefaultImgDirEnum.NETWORK_EQUIPMENT,DefaultImgDirEnum.OPERATION_SYSTEM,
                    DefaultImgDirEnum.SAFETY_EQUIPMENT, DefaultImgDirEnum.COMMON_TARGET,
                    DefaultImgDirEnum.SAFETY, DefaultImgDirEnum.SERVER,
                    DefaultImgDirEnum.INDUSTRIAL_CONTROL, DefaultImgDirEnum.EXCHANGE_1,
                    DefaultImgDirEnum.EXCHANGE_2, DefaultImgDirEnum.ROUTER_1,
                    DefaultImgDirEnum.ROUTER_2, DefaultImgDirEnum.GATEWAY,
                    DefaultImgDirEnum.WIRELESS, DefaultImgDirEnum.BIZ);
            fileNameList = getFileByDefaultImgDirEnums(defaultImgDirEnums);
        }else{
            fileNameList = defaultImgService.getFileByDefaultImgDirEnum(defaultImgDirEnum);
        }
        return ResultCommon.success(fileNameList);
    }

    @ApiOperation("获取系统图片")
    @GetMapping("/get-sys-logo-img")
    public ServiceResult<Map<String, String>> getSysLogoImg() {
        if (StringUtils.isBlank(AggregationSingleton.aggregationConfig.getStaticLocation())) {
            throw new MpRuntimeException(BaseErrorCode.DATA_NOT_EXIST);
        }
        List<String> groupList = Collections.singletonList(DefaultImgDirEnum.SYS_LOGO.getGroup());
        List<LocalFileMappingModel> modelList = DefaultImgUtil.listByGroups(groupList);
        Map<String, String> urlMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(modelList)) {
            modelList.forEach(model -> urlMap.put(model.getName(), FsCommon.genHttpUrl(model.getRemoteFile())));
        }

        return ResultCommon.success(urlMap);
    }

    /**
     * 获取多个指定目录下的默认图片
     *
     * @param defaultImgDirEnums
     * @return
     */
    private List<String> getFileByDefaultImgDirEnums(List<DefaultImgDirEnum> defaultImgDirEnums) {
        return defaultImgDirEnums.stream().map(v -> defaultImgService.getFileByDefaultImgDirEnum(v)).flatMap(List::stream).collect(Collectors.toList());
    }


}
