//package cn.cyberpeace.mp.devops.cluster.cloud.platform.service.impl;
//
//import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.ScenariosService;
//import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
//import cn.cyberpeace.mp.devops.cluster.model.*;
//import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.stereotype.Service;
//
///**
// * @author wanggang
// */
//@Service
//public class ScenariosServiceImpl extends BaseOpenStackServiceImpl implements ScenariosService {
//
//    public ScenariosServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
//        super(devopsConfig, objectMapper);
//    }
//
//    @Override
//    public BaseCloudPlatformModel create(ScenariosSnapshot snapshotCreate) {
//        return super.sendPost(  URL_VERSION + SCENARIOS + "/device_snapshot", JsonUtils.toUnderlineJSONString(snapshotCreate), BaseCloudPlatformModel.class);
//    }
//
//
//    @Override
//    public BaseCloudPlatformModel injectFile(DeviceInjectFileCreate injectFileCreate) {
//        return super.sendPost(  URL_VERSION + SCENARIOS + "/device_inject_file", JsonUtils.toUnderlineJSONString(injectFileCreate), BaseCloudPlatformModel.class);
//    }
//
//    @Override
//    public BaseCloudPlatformModel deleteInjectFile(DeviceInjectFileDelete delete) {
//        return super.sendDelete(  URL_VERSION + SCENARIOS + "/device_inject_file", JsonUtils.toUnderlineJSONString(delete), BaseCloudPlatformModel.class);
//    }
//
//    @Override
//    public BaseCloudPlatformModel create(ImagesCreateInstanceCloudPlatform snapshotCreate) {
//        return super.sendPost(  URL_VERSION + SCENARIOS + "/device_scenario", JsonUtils.toUnderlineJSONString(snapshotCreate), BaseCloudPlatformModel.class);
//    }
//
//    @Override
//    public BaseCloudPlatformModel mergeSnapshot(DeviceSnapshotMergeCloudPlatform mergeCloudPlatform) {
//        return super.sendPost(  URL_VERSION + SCENARIOS + "/device_snapshot_merge", JsonUtils.toUnderlineJSONString(mergeCloudPlatform), BaseCloudPlatformModel.class);
//    }
//
//    @Override
//    public BaseCloudPlatformModel delete(ImagesDeleteInstanceCloudPlatform cloudPlatform) {
//        return super.sendDelete(  URL_VERSION + SCENARIOS + "/device_scenario", JsonUtils.toUnderlineJSONString(cloudPlatform), BaseCloudPlatformModel.class);
//    }
//}
