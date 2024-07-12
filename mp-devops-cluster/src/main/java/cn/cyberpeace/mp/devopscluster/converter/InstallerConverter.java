 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name InstallerConverter.java.
 // File size 8768.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.auth.util.DefaultImgUtil;
import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
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

/**
 * @ClassName InstallerConverter
 * @Description 服务管理
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseConverter
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstallerConverter implements BaseConverter{

    private final InstallerService installerService;

    /**
     * 分页查询服务列表
     * @param request
     * @param ignoreFields
     * @return
     */
    public BasePageBO<InstallerConvertModel> page(InstallerPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(Boolean.TRUE,request,ignoreFields);
        CloudPlatformPageResult<Installer> model = installerService.page(getParams);
        BasePageBO<InstallerConvertModel> convertModel = new BasePageBO<>();
        List<InstallerConvertModel> modelList = new ArrayList<>();
        for (Installer installer : model.getList()) {
            InstallerConvertModel installerConvertModel = convertInstaller(installer);
            modelList.add(installerConvertModel);
        }
        convertModel.setList(modelList);
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
    public BaseCloudPlatformResult delete(InstallerDeleteRequest request) {
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
            for (Resource resource : model.getResourceList()) {
                if (StringUtils.isNotBlank(resource.getPlatforms())) {
                    resource.setPlatforms(Joiner.on("|").join(resource.getPlatforms().split("")));
                }
            }
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
        installer.setLogo(DefaultImgUtil.fdfsUrlToPath(request.getLogo()));
        installer.setTmpdirUuid(Objects.nonNull(request.getDockerInstallerData()) ? request.getDockerInstallerData().getTmpdirUuid() : null);
        installer.setUserFilename(Objects.nonNull(request.getDockerInstallerData()) ? request.getDockerInstallerData().getUserFilename() : null);
        if (StringUtils.isNotBlank(request.getCategories())){
            boolean seen = false;
            String acc = null;
            for (String s : request.getCategories().split("\\|")) {
                if (!seen) {
                    seen = true;
                    acc = s;
                } else {
                    acc = acc.concat(s);
                }
            }
            installer.setCategories(seen ? acc : "");
        }
        installer.setDeployType(request.getDeployType());
        if (Objects.nonNull(request.getDockerInstallerData())
                && Objects.nonNull(request.getDockerInstallerData().getStartUpParameterList())) {
            installer.setSettings(JsonUtils.snakeStringify(request.getDockerInstallerData().getStartUpParameterList()));
        }
        if (Objects.nonNull(request.getResourceList())) {
            for (Resource resource : request.getResourceList()) {
                resource.setFile(UUID.randomUUID().toString());
                if (StringUtils.isNotBlank(resource.getPlatforms())) {
                    boolean seen = false;
                    String acc = null;
                    for (String s : resource.getPlatforms().split("\\|")) {
                        if (!seen) {
                            seen = true;
                            acc = s;
                        } else {
                            acc = acc.concat(s);
                        }
                    }
                    resource.setPlatforms(seen ? acc : "");
                }
            }
            installer.setResources(JsonUtils.snakeStringify(request.getResourceList()));
        }
        return installer;
    }
}
