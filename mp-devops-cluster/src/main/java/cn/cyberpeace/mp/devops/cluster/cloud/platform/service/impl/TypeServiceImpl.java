//package cn.cyberpeace.mp.devops.cluster.cloud.platform.service.impl;
//
//import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.TypeService;
//import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
//import cn.cyberpeace.mp.devops.cluster.model.*;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.stereotype.Service;
//
///**
// * @author wanggang
// */
//@Service
//public class TypeServiceImpl extends BaseOpenStackServiceImpl implements TypeService {
//
//    public TypeServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
//        super(devopsConfig, objectMapper);
//    }
//
//    @Override
//    public TypeCloudPlatformModel findL1AndL2AndL3Type() {
//        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + "?type=l3", TypeCloudPlatformModel.class);
//    }
//
//    @Override
//    public TypeL2CloudPlatformModel findL1AndL2Type() {
//        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + "?type=l2", TypeL2CloudPlatformModel.class);
//    }
//
//    @Override
//    public AccessProtocolsPortCloudPlatform findAccessProtocolsPort() {
//        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + "?type=access_protocols_port", AccessProtocolsPortCloudPlatform.class);
//    }
//
//
//    @Override
//    public TypeIdIntegerCloudPlatformModel findIdIntegerByType(String type) {
//        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + "?type=" + type, TypeIdIntegerCloudPlatformModel.class);
//    }
//
//    @Override
//    public TypeIdStringCloudPlatformModel findIdStringByType(String type) {
//        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + "?type=" + type, TypeIdStringCloudPlatformModel.class);
//    }
//}
