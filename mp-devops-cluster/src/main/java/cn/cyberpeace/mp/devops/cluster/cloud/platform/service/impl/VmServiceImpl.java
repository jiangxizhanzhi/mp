//package cn.cyberpeace.mp.devops.cluster.cloud.platform.service.impl;
//
//import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.InstanceOsServicePageResult;
//import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.InstancePageResult;
//import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformPageResult;
//import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.VmService;
//import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
//import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
//import cn.cyberpeace.mp.devops.cluster.model.InstanceCloudPlatformModel;
//import cn.cyberpeace.mp.devops.cluster.model.InstanceOsServiceCloudPlatformModel;
//import cn.cyberpeace.mp.devops.cluster.model.VmActionCloudPlatformModel;
//import cn.cyberpeace.mp.devops.cluster.model.common.InstanceModel;
//import cn.cyberpeace.mp.devops.cluster.model.common.InstanceOsServiceModel;
//import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.common.collect.Lists;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections4.CollectionUtils;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Objects;
//
///**
// * 运维管控-系统运维-计算实例管理-虚拟机
// * @author wanggang
// */
//@Slf4j
//@Service
//public class VmServiceImpl extends BaseOpenStackServiceImpl implements VmService {
//
//    public VmServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
//        super(devopsConfig,objectMapper);
//    }
//
//    @Override
//    public InstanceCloudPlatformModel page(String getParams) {
//        InstancePageResult pageResult = super.sendGet(URL_VERSION + INSTANCES + getParams, InstancePageResult.class);
//        if (Objects.isNull(pageResult) || Objects.isNull(pageResult.getData())){
//            return null;
//        }
//        CloudPlatformPageResult<InstanceModel> data = pageResult.getData();
//        List<InstanceModel> instanceModels = data.getList();
//        InstanceCloudPlatformModel model = new InstanceCloudPlatformModel();
//        model.setInstances(CollectionUtils.isEmpty(instanceModels) ? Lists.newArrayList() : instanceModels);
//        model.setTotal(data.getTotal());
//        model.setCode(pageResult.getCode());
//        return model;
//    }
//
//    @Override
//    public BaseCloudPlatformModel action(VmActionCloudPlatformModel model, String uuid) {
//        return super.sendPost(URL_VERSION + INSTANCES + uuid  + "/action", JsonUtils.toUnderlineJSONString(model),BaseCloudPlatformModel.class);
//    }
//
//    /**
//     *
//     * @return
//     */
//    @Override
//    public InstanceOsServiceCloudPlatformModel osServicesStateFromInstances(){
//        InstanceOsServicePageResult pageResult = super.sendGet(URL_VERSION + INSTANCES + "/os-services", InstanceOsServicePageResult.class);
//        if (Objects.isNull(pageResult) || Objects.isNull(pageResult.getData())){
//            return null;
//        }
//        CloudPlatformPageResult<InstanceOsServiceModel> data = pageResult.getData();
//        List<InstanceOsServiceModel> list = data.getList();
//        InstanceOsServiceCloudPlatformModel model = new InstanceOsServiceCloudPlatformModel();
//        model.setServices(CollectionUtils.isEmpty(list) ? Lists.newArrayList() : list);
//        model.setTotal(data.getTotal());
//        model.setCode(pageResult.getCode());
//        return model;
//    }
//}
