package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.*;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.InstallerService;
import cn.cyberpeace.mp.devops.cluster.converter.model.InstallerConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.InstallerConvertPageModel;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.InstallerCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.InstallerFormDataCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.InstallerDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.InstallerPageRequest;
import cn.cyberpeace.mp.devops.cluster.request.InstallerRequest;
import cn.cyberpeace.mp.devops.cluster.utils.FileUtils;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 服务管理
 * @author wanggang
 */
@Slf4j
@Service
public class InstallerConverter implements BaseConverter{

    private InstallerService installerService;

    @Autowired
    public InstallerConverter(InstallerService installerService) {
        this.installerService = installerService;
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(InstallerPageRequest::getPage,InstallerPageRequest::getPerPage);
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(InstallerPageRequest::getLimit,InstallerPageRequest::getOffset,
                InstallerPageRequest::getPage,InstallerPageRequest::getPerPage);
    }

    /**
     * 分页查询服务列表
     * @param request
     * @param ignoreFields
     * @return
     */
    public InstallerConvertPageModel page(InstallerPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(Boolean.TRUE,request,ignoreFields);
        InstallerCloudPlatformModel model = installerService.page(getParams);
        InstallerConvertPageModel convertModel = new InstallerConvertPageModel();
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getItems())){
            convertModel.setTotal(0);
            convertModel.setModelList(Lists.newArrayList());
            return convertModel;
        }
        List<InstallerConvertModel> modelList = model.getItems().stream()
                .map(this::convertInstaller).collect(Collectors.toList());
        convertModel.setModelList(modelList);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

    /**
     * 根据ID查询服务信息
     * @param id
     * @return
     */
    public InstallerConvertModel findById(String id) {
        Installer installer = installerService.findById(id);
        return convertInstaller(installer);
    }

    /**
     * 新增服务信息
     * @param request
     * @return
     */
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

    /**
     * 删除服务信息
     * @param request
     * @return
     */
    public BaseCloudPlatformModel delete(InstallerDeleteRequest request) {
        return installerService.deleteByIds(request.getUuids());
    }

    /**
     * 转换Installer
     * @param installer
     * @return
     */
    private InstallerConvertModel convertInstaller(Installer installer){
        InstallerConvertModel model = new InstallerConvertModel();
        if (Objects.isNull(installer)){
            return model;
        }
        model.setUuid(installer.getUuid());
        model.setName(installer.getName());
        model.setDescription(installer.getDescription());
        model.setLogo(installer.getLogo());
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

    /**
     * 转换InstallerRequest
     * @param request
     * @return
     */
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
