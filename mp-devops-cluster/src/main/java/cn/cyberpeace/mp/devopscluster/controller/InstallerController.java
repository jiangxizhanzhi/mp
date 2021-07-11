package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtDocker;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.InstallerConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.converter.model.InstallerConvertModel;
import cn.cyberpeace.mp.devopscluster.request.DockerInstallerData;
import cn.cyberpeace.mp.devopscluster.request.InstallerDeleteRequest;
import cn.cyberpeace.mp.devopscluster.request.InstallerPageRequest;
import cn.cyberpeace.mp.devopscluster.request.InstallerRequest;
import cn.cyberpeace.mp.devopscluster.service.upload.InstallerDockerUploadService;
import cn.cyberpeace.mp.devopscluster.vo.InstallerByIdVO;
import cn.cyberpeace.mp.devopscluster.vo.InstallerPageVO;
import cn.cyberpeace.mp.devopscluster.vo.TargetUploadVO;
import cn.cyberpeace.mp.devopscluster.vo.common.PageResult;
import cn.cyberpeace.mp.devopscluster.vo.common.Result;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;



@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/service")
@Api(tags = "靶标管理-服务管理")
@RequiredArgsConstructor
public class InstallerController {

    private final InstallerConverter installerConverter;

    @NonNull
    private InstallerDockerUploadService installerDockerUploadService;

    
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public PageResult<InstallerPageVO> page(InstallerPageRequest request){
        BasePageBO<InstallerConvertModel> model = installerConverter.page(request);

        List<InstallerPageVO> list = model.getList().stream().map(installer ->
                InstallerPageVO.builder().uuid(installer.getUuid()).name(installer.getName())
                        .logo(installer.getLogo()).description(installer.getDescription())
                        .categories(installer.getCategories()).deployType(installer.getDeployType())
                        .startUpParameterList(installer.getStartUpParameterList())
                        .file(Objects.nonNull(installer.getExtDocker()) ? installer.getExtDocker().getFile() : null)
                        .build()
        ).collect(Collectors.toList());

        return new PageResult<>(list,model.getTotal());
    }

    @ApiOperation("根据ID查询")
    @GetMapping("/{id}")
    public Result<InstallerByIdVO> getById(@PathVariable String id){
        InstallerConvertModel model = installerConverter.findById(id);
        if (Objects.isNull(model)){
            return new Result<>();
        }
        ExtDocker extDocker = model.getExtDocker();
        InstallerByIdVO response = InstallerByIdVO.builder().uuid(model.getUuid())
                .name(model.getName()).description(model.getDescription()).logo(model.getLogo())
                .categories(model.getCategories())
                .deployType(model.getDeployType())
                .dockerInstallerData(new DockerInstallerData(model.getStartUpParameterList(),Objects.nonNull(extDocker) ? extDocker.getFile() : null, Objects.nonNull(extDocker) ? extDocker.getUserFilename() : null))
                .resourceList(model.getResourceList())
                .deviceCount(model.getDeviceCount())
                .build();
        return new Result<>(response);
    }

    
    @ApiOperation("新增")
    @ApiImplicitParam(name = "request", value = "新增数据请求体", dataTypeClass = InstallerRequest.class)
    @PostMapping
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.CREATE, title = "新增服务", target = OpTargetEnums.SERVICE)
    public ServiceResult add(@RequestBody InstallerRequest request){
        installerConverter.add(request);
        return ResultCommon.success();
    }

    
    @ApiOperation("更新")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "更新数据请求体", dataTypeClass = InstallerRequest.class),
            @ApiImplicitParam(name = "uuid", value = "靶标ID", dataTypeClass = String.class)
    })

    @PatchMapping("/{uuid}")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.UPDATE, title = "修改服务", target = OpTargetEnums.SERVICE)
    public ServiceResult update(@RequestBody InstallerRequest request,@PathVariable("uuid") String uuid){
        request.setUuid(uuid);
        if (StringUtils.isBlank(request.getUuid())){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        installerConverter.add(request);
        return ResultCommon.success();
    }

    
    @ApiOperation("删除")
    @ApiImplicitParam(name = "request", value = "删除数据请求体", dataTypeClass = InstallerDeleteRequest.class)
    @DeleteMapping
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.DELETE, title = "删除服务", target = OpTargetEnums.SERVICE)
    public ServiceResult delete(@RequestBody InstallerDeleteRequest request){
        installerConverter.delete(request);
        return ResultCommon.success();
    }

    
    @ApiOperation("自定义上传Docker文件方法")
    @PostMapping(value = "/upload/docker")
    @ResponseBody
    public ServiceResult<TargetUploadVO> customUpload(MultipartFile file, HttpServletRequest request) {
        installerDockerUploadService = installerDockerUploadService.build(file);
        return installerDockerUploadService.upload();
    }
}
