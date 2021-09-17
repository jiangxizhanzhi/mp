package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.converter.InstallerConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.InstallerConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.InstallerConvertPageModel;
import cn.cyberpeace.mp.devops.cluster.model.Installer;
import cn.cyberpeace.mp.devops.cluster.model.InstallerCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.InstallerFormDataCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtDocker;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtNormal;
import cn.cyberpeace.mp.devops.cluster.model.common.Resource;
import cn.cyberpeace.mp.devops.cluster.model.common.StartUpParameter;
import cn.cyberpeace.mp.devops.cluster.request.InstallerDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.InstallerPageRequest;
import cn.cyberpeace.mp.devops.cluster.request.InstallerRequest;
import cn.cyberpeace.mp.devops.cluster.service.InstallerService;
import cn.cyberpeace.mp.devops.cluster.service.TargetLogoService;
import cn.cyberpeace.mp.devops.cluster.utils.FileUtils;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InstallerConverterImpl implements InstallerConverter {

    private InstallerService installerService;

    private TargetLogoService targetLogoService;

    @Autowired
    public InstallerConverterImpl(InstallerService installerService,TargetLogoService targetLogoService) {
        this.installerService = installerService;
        this.targetLogoService = targetLogoService;
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return new String[0];
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return new String[0];
    }

    @Override
    public InstallerConvertPageModel page(InstallerPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = BeanUtils.wrapperGetParamsByObject(Boolean.TRUE,request,ignoreFields);
        InstallerCloudPlatformModel model = installerService.page(getParams);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getItems())){
            return null;
        }
        List<InstallerConvertModel> modelList = model.getItems().stream()
                .map(installer -> convertInstaller(installer)).collect(Collectors.toList());

        InstallerConvertPageModel convertModel = new InstallerConvertPageModel();
        convertModel.setModelList(modelList);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

    @Override
    public InstallerConvertModel findById(String id) {
        Installer installer = installerService.findById(id);
        return convertInstaller(installer);
    }

    @Override
    public InstallerConvertModel add(InstallerRequest request) {
//        if (Objects.nonNull(request.getLogoFile()) && !request.getLogoFile().getClass().isAssignableFrom(String.class)) {
//            String logo = targetLogoService.uploadLogo((MultipartFile) request.getLogoFile());
//            request.setLogo(logo);
//        }
        if (Objects.nonNull(request.getLogoFile()) && request.getLogoFile().getSize() > 0 ){
            String logo = targetLogoService.uploadLogo(request.getLogoFile());
            request.setLogo(logo);
        }
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

//    @Deprecated
//    @Override
//    public InstallerConvertModel update(InstallerRequest request) {
//        Installer installer = convertRequest(request);
//        Installer result = installerService.update(request.getUuid(),installer);
//        return convertInstaller(result);
//    }

    @Override
    public Boolean delete(InstallerDeleteRequest request) {
        return installerService.deleteByIds(request.getUuids());
    }

    /**
     * 转换Installer
     * @param installer
     * @return
     */
    private InstallerConvertModel convertInstaller(Installer installer){
        if (Objects.isNull(installer)){
            return null;
        }
        InstallerConvertModel model = new InstallerConvertModel();
        model.setUuid(installer.getUuid());
        model.setName(installer.getName());
        model.setDescription(installer.getDescription());
        model.setLogo(installer.getLogo());
        if (StringUtils.isNotBlank(installer.getCategories())){
            model.setCategories(Joiner.on("|").join(installer.getCategories().split("")));
        }
        model.setDeployType(installer.getDeployType());
        ExtDocker extDocker = installer.getExtDocker();
        model.setStartUpParameterList(extDocker.getSettings());
        model.setExtDocker(extDocker);
        ExtNormal extNormal = installer.getExtNormal();
        if (Objects.nonNull(extNormal) && StringUtils.isNotBlank(extNormal.getResources())){
            model.setResourceList(JSONArray.parseArray(extNormal.getResources(), Resource.class));
            model.getResourceList().stream().filter(resource -> StringUtils.isNotBlank(resource.getPlatforms())).forEach(resource -> {
                resource.setPlatforms(Joiner.on("|").join(resource.getPlatforms().split("")));
            });
        }
        model.setExtNormal(extNormal);
        model.setSuccess(installer.isSuccess());
        model.setMessage(installer.getMessage());
        model.setMessageZh(installer.getMessageZh());
        return model;
    }

    private InstallerFormDataCloudPlatformModel convertInstallerRequest(InstallerRequest request){
        InstallerFormDataCloudPlatformModel installer = new InstallerFormDataCloudPlatformModel();
        installer.setName(request.getName());
        installer.setDescription(request.getDescription());
        installer.setLogo(request.getLogo());
        installer.setTmpdirUuid(Objects.nonNull(request.getDockerInstallerData()) ? request.getDockerInstallerData().getTmpdirUuid() : null);
        installer.setUserFilename(Objects.nonNull(request.getDockerInstallerData()) ? request.getDockerInstallerData().getUserFilename() : null);
        if (StringUtils.isNotBlank(request.getCategories())){
            installer.setCategories(Arrays.stream(request.getCategories().split("\\|")).reduce((a,b) -> a.concat(b)).orElse(""));
        }
        installer.setDeployType(request.getDeployType());
        if (Objects.nonNull(request.getDockerInstallerData())
                && CollectionUtils.isNotEmpty(request.getDockerInstallerData().getStartUpParameterList())) {
            installer.setSettings(JsonUtils.toUnderlineJSONString(request.getDockerInstallerData().getStartUpParameterList()));
        }
        if (CollectionUtils.isNotEmpty(request.getResourceList())) {
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

    /**
     * 转换InstallerRequest
     * @param request
     * @return
     */
//    private Installer convertRequest(InstallerRequest request){
//        Installer installer = new Installer();
//        installer.setName(request.getName());
//        installer.setDescription(request.getDescription());
//        installer.setLogo(request.getLogo());
//        installer.setCategories(String.join(",",request.getCategories()));
//        installer.setDeployType(request.getDeployType());
////        ExtDocker extDocker = request.getExtDocker();
////        if (Objects.nonNull(extDocker) && CollectionUtils.isNotEmpty(request.getStartUpParameterList())) {
////            extDocker.setSettings(JSONArray.toJSONString(request.getStartUpParameterList()));
////        }
////        installer.setExtDocker(extDocker);
////        ExtNormal extNormal = request.getExtNormal();
////        if (Objects.nonNull(extNormal) && CollectionUtils.isNotEmpty(request.getResourceList())) {
//////            extNormal.setResources(JSONArray.toJSONString(request.getResourceList()));
////        }
//        installer.setResourceList(request.getResourceList());
////        installer.setExtNormal(extNormal);
//        return installer;
//    }
}
