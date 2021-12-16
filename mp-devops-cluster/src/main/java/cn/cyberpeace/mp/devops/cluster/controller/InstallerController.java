package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.InstallerConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.InstallerConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.InstallerConvertPageModel;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtDocker;
import cn.cyberpeace.mp.devops.cluster.request.DockerInstallerData;
import cn.cyberpeace.mp.devops.cluster.request.InstallerDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.InstallerPageRequest;
import cn.cyberpeace.mp.devops.cluster.request.InstallerRequest;
import cn.cyberpeace.mp.devops.cluster.response.InstallerByIdResponse;
import cn.cyberpeace.mp.devops.cluster.response.InstallerPageResponse;
import cn.cyberpeace.mp.devops.cluster.response.base.PageResult;
import cn.cyberpeace.mp.devops.cluster.response.base.Result;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * 服务管理
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/service")
@Api(tags = "靶标管理-服务管理")
public class InstallerController {

    private InstallerConverter installerConverter;

    @Autowired
    public InstallerController(InstallerConverter installerConverter) {
        this.installerConverter = installerConverter;
    }

    /**
     * 分页查询
     */
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public PageResult<InstallerPageResponse> page(InstallerPageRequest request){
        InstallerConvertPageModel model = installerConverter.page(request);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getModelList())){
            return new PageResult<>(Lists.newArrayList(), NumberUtils.INTEGER_ZERO);
        }

        List<InstallerPageResponse> list = model.getModelList().stream().map(installer ->
                InstallerPageResponse.builder().uuid(installer.getUuid()).name(installer.getName())
                        .logo(installer.getLogo()).description(installer.getDescription())
                        .categories(installer.getCategories()).deployType(installer.getDeployType())
                        .build()
        ).collect(Collectors.toList());

        return new PageResult<>(list,model.getTotal());
    }

    @ApiOperation("根据ID查询")
    @GetMapping("/{id}")
    public Result<InstallerByIdResponse> getById(@PathVariable String id){
        InstallerConvertModel model = installerConverter.findById(id);
        if (Objects.isNull(model)){
            return new Result<>();
        }
        ExtDocker extDocker = model.getExtDocker();
        InstallerByIdResponse response = InstallerByIdResponse.builder().uuid(model.getUuid())
                .name(model.getName()).description(model.getDescription()).logo(model.getLogo())
                .categories(model.getCategories())
                .deployType(model.getDeployType())
                .dockerInstallerData(new DockerInstallerData(model.getStartUpParameterList(),Objects.nonNull(extDocker) ? extDocker.getFile() : null, Objects.nonNull(extDocker) ? extDocker.getUserFilename() : null))
                .resourceList(model.getResourceList())
                .deviceCount(model.getDeviceCount())
                .build();
        return new Result<>(response);
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping
    public ServiceResult add(@RequestBody InstallerRequest request, HttpServletRequest httpServletRequest){
        InstallerConvertModel model = installerConverter.add(request);
        if (Objects.isNull(model)){
            return ServiceResult.fail("");
        }
        String acceptLanguage = httpServletRequest.getHeader("Accept-Language");
        return model.isSuccess() ? ServiceResult.successful() : StringUtils.isNotBlank(acceptLanguage) && acceptLanguage.equals("zh-CN") ?
                ServiceResult.fail(model.getMsgZh()) : ServiceResult.fail(model.getMsg());
    }

    /**
     * 更新
     */
    @ApiOperation("更新")
    @PatchMapping("/{uuid}")
    public ServiceResult update(@RequestBody InstallerRequest request,@PathVariable("uuid") String uuid, HttpServletRequest httpServletRequest){
        request.setUuid(uuid);
        if (StringUtils.isBlank(request.getUuid())){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        InstallerConvertModel model = installerConverter.add(request);
        if (Objects.isNull(model)){
            return ServiceResult.fail("");
        }
        String acceptLanguage = httpServletRequest.getHeader("Accept-Language");
        return model.isSuccess() ? ServiceResult.successful() : StringUtils.isNotBlank(acceptLanguage) && acceptLanguage.equals("zh-CN") ?
                ServiceResult.fail(model.getMsgZh()) : ServiceResult.fail(model.getMsg());
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @DeleteMapping
    public ServiceResult delete(@RequestBody InstallerDeleteRequest request){
        BaseCloudPlatformModel model = installerConverter.delete(request);
        if (Objects.isNull(model)){
            return ServiceResult.fail("");
        }
        return model.isSuccess() ? ServiceResult.successful() : ServiceResult.fail(model.getMessageZh());
    }
}
