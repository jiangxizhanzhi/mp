package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.auth.util.DefaultImgUtil;
import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.*;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.InstallerService;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.converter.model.InstallerConvertModel;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.InstallerFormDataCloudPlatformModel;
import cn.cyberpeace.mp.devopscluster.request.InstallerDeleteRequest;
import cn.cyberpeace.mp.devopscluster.request.InstallerPageRequest;
import cn.cyberpeace.mp.devopscluster.request.InstallerRequest;
import cn.cyberpeace.mp.devopscluster.utils.FileUtils;
import cn.cyberpeace.mp.devopscluster.utils.JsonUtils;
import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Joiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class InstallerConverter implements BaseConverter{

    private final InstallerService installerService;

    
    public BasePageBO<InstallerConvertModel> page(InstallerPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(Boolean.TRUE,request,ignoreFields);
        CloudPlatformPageResult<Installer> model = installerService.page(getParams);
        BasePageBO<InstallerConvertModel> convertModel = new BasePageBO<>();
        List<InstallerConvertModel> modelList = model.getList().stream()
                .map(this::convertInstaller).collect(Collectors.toList());
        convertModel.setList(modelList);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

    
    public InstallerConvertModel findById(String id) {
        Installer installer = installerService.findById(id);
        return convertInstaller(installer);
    }

    
    public InstallerConvertModel add(InstallerRequest request) {
        InstallerFormDataCloudPlatformModel installer = convertInstallerRequest(request);
        installer.setUuid(request.getUuid());
        List<MultipartFile> multipartFiles = new ArrayList<>();
        if (Objects.nonNull(request.getDockerInstallerData()) &&
                Objects.nonNull(request.getDockerInstallerData().getDockerFile()) && request.getDockerInstallerData().getDockerFile().getSize() > 0){
            multipartFiles.add(request.getDockerInstallerData().getDockerFile());
        }else{
            multipartFiles = request.getMultipartFiles();
        }
        Installer result = null;
        try {
            result = installerService.add(installer, FileUtils.transferToFile(multipartFiles));
        } catch (IOException e) {
            log.error("add installer error, message : {}",e);
        }
        return convertInstaller(result);
    }

    
    public BaseCloudPlatformResult delete(InstallerDeleteRequest request) {
        return installerService.deleteByIds(request.getUuids());
    }

    
    private InstallerConvertModel convertInstaller(Installer installer){
        InstallerConvertModel model = new InstallerConvertModel();
        if (Objects.isNull(installer)){
            return model;
        }
        model.setUuid(installer.getUuid());
        model.setName(installer.getName());
        model.setDescription(installer.getDescription());
        model.setLogo(DefaultImgUtil.pathToFdfsUrl(installer.getLogo()));
        if (StringUtils.isNotBlank(installer.getCategories())){
            model.setCategories(Joiner.on("|").join(installer.getCategories().split("")));
        }
        model.setDeployType(installer.getDeployType());
        ExtDocker extDocker = installer.getExtDocker();
        if (Objects.nonNull(extDocker) && StringUtils.isNotBlank(extDocker.getSettings())){
            model.setStartUpParameterList(JSONArray.parseArray(extDocker.getSettings(), StartUpParameter.class));
        }
        model.setExtDocker(extDocker);
        ExtNormal extNormal = installer.getExtNormal();
        if (Objects.nonNull(extNormal) && StringUtils.isNotBlank(extNormal.getResources())){
            model.setResourceList(JSONArray.parseArray(extNormal.getResources(), Resource.class));
            model.getResourceList().stream().filter(resource -> StringUtils.isNotBlank(resource.getPlatforms())).forEach(resource ->
                resource.setPlatforms(Joiner.on("|").join(resource.getPlatforms().split("")))
            );
        }
        model.setDeviceCount(installer.getDeviceCount());
        model.setExtNormal(extNormal);
        model.setSuccess(installer.isSuccess());
        model.setMessage(installer.getMessage());
        model.setMessageZh(installer.getMessageZh());
        model.setMsg(installer.getMsg());
        model.setMsgZh(installer.getMsgZh());
        return model;
    }

    
    private InstallerFormDataCloudPlatformModel convertInstallerRequest(InstallerRequest request){
        InstallerFormDataCloudPlatformModel installer = new InstallerFormDataCloudPlatformModel();
        installer.setName(request.getName());
        installer.setDescription(request.getDescription());
        installer.setLogo(DefaultImgUtil.fdfsUrlToPath(request.getLogo()));
        installer.setTmpdirUuid(Objects.nonNull(request.getDockerInstallerData()) ? request.getDockerInstallerData().getTmpdirUuid() : null);
        installer.setUserFilename(Objects.nonNull(request.getDockerInstallerData()) ? request.getDockerInstallerData().getUserFilename() : null);
        if (StringUtils.isNotBlank(request.getCategories())){
            installer.setCategories(Arrays.stream(request.getCategories().split("\\|")).reduce((a,b) -> a.concat(b)).orElse(""));
        }
        installer.setDeployType(request.getDeployType());
        if (Objects.nonNull(request.getDockerInstallerData())
                && Objects.nonNull(request.getDockerInstallerData().getStartUpParameterList())) {
            installer.setSettings(JsonUtils.toUnderlineJSONString(request.getDockerInstallerData().getStartUpParameterList()));
        }
        if (Objects.nonNull(request.getResourceList())) {
            request.getResourceList().forEach(resource -> {
                resource.setFile(UUID.randomUUID().toString());
                if (StringUtils.isNotBlank(resource.getPlatforms())){
                    resource.setPlatforms(Arrays.stream(resource.getPlatforms().split("\\|")).reduce((a,b) -> a.concat(b)).orElse(""));
                }
            });
            installer.setResources(JsonUtils.toUnderlineJSONString(request.getResourceList()));
        }
        return installer;
    }
}
