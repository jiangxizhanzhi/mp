package cn.cyberpeace.mp.aggregation.controller;

import cn.cyberpeace.mp.aggregation.constant.AggregationSingleton;
import cn.cyberpeace.mp.aggregation.constant.ControllerConstant;
import cn.cyberpeace.mp.aggregation.dto.cluster.DefaultImgTarget;
import cn.cyberpeace.mp.aggregation.request.DefaultImgRequest;
import cn.cyberpeace.mp.aggregation.service.DefaultImgService;
import cn.cyberpeace.mp.auth.constant.DefaultImgDirEnum;
import cn.cyberpeace.mp.auth.constant.DefaultImgStyleEnum;
import cn.cyberpeace.mp.auth.util.DefaultImgUtil;
import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.constant.TargetEnum;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wanggang
 */
@Api(value = "默认图片接口", tags = "默认图片接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/default-img")
@AuthTarget(TargetEnum.NO_AUTH)
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
        List<String> groupList = Collections.singletonList(defaultImgDirEnum.getGroup());
        List<LocalFileMappingModel> modelList = DefaultImgUtil.listByGroups(groupList);
        List<String> urlList =
                new ArrayList<>();
        for (LocalFileMappingModel model : modelList) {
            String s = FsCommon.genHttpUrl(model.getRemoteFile());
            urlList.add(s);
        }

        return ResultCommon.success(urlList);
    }

    /**
     * 获取靶标图集
     *
     * @return
     */
    @ApiOperation("获取靶标图集")
    @GetMapping("/get-default-img-target")
    public ServiceResult<List<DefaultImgTarget>> getDefaultImgTarget(){
        List<DefaultImgTarget> rtnList = new ArrayList<>();
        if (StringUtils.isBlank(AggregationSingleton.aggregationConfig.getStaticLocation())){
            throw new MpRuntimeException(BaseErrorCode.DATA_NOT_EXIST);
        }
        for (DefaultImgStyleEnum styleEnum : DefaultImgStyleEnum.values()) {
            List<String> groupList = Collections.singletonList(styleEnum.getGroup());
            List<LocalFileMappingModel> modelList = DefaultImgUtil.listByGroups(groupList);
            DefaultImgTarget defaultImgTarget = new DefaultImgTarget();
            List<String> urlList = new ArrayList<>();
            for (LocalFileMappingModel model : modelList) {
                String s = FsCommon.genHttpUrl(model.getRemoteFile());
                urlList.add(s);
            }
            defaultImgTarget.setImgs(urlList);
            defaultImgTarget.setStyle(styleEnum.getStyle());
            defaultImgTarget.setName(styleEnum.getName());
            rtnList.add(defaultImgTarget);
        }
        return ResultCommon.success(rtnList);
    }

    @ApiOperation("获取系统图片")
    @GetMapping("/get-sys-logo-img")
    public ServiceResult<Map<String, String>> getSysLogoImg(){
        if (StringUtils.isBlank(AggregationSingleton.aggregationConfig.getStaticLocation())){
            throw new MpRuntimeException(BaseErrorCode.DATA_NOT_EXIST);
        }
        List<String> groupList = Collections.singletonList(DefaultImgDirEnum.SYS_LOGO.getGroup());
        List<LocalFileMappingModel> modelList = DefaultImgUtil.listByGroups(groupList);
        Map<String, String> urlMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(modelList)) {
            for (LocalFileMappingModel model : modelList) {
                urlMap.put(model.getName(), FsCommon.genHttpUrl(model.getRemoteFile()));
            }
        }

        return ResultCommon.success(urlMap);
    }
}
