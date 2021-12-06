//package cn.cyberpeace.mp.devops.cluster.cloud.platform.service.impl;
//
//import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.FirewallPageResult;
//import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformPageResult;
//import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.FirewallService;
//import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
//import cn.cyberpeace.mp.devops.cluster.model.FirewallCloudPlatformModel;
//import cn.cyberpeace.mp.devops.cluster.model.common.Firewall;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.common.collect.Lists;
//import org.apache.commons.collections4.CollectionUtils;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Objects;
//
///**
// * 运维管控-系统运维-防火墙
// * @author wanggang
// */
//@Service
//public class FirewallServiceImpl extends BaseOpenStackServiceImpl implements FirewallService {
//
//    public FirewallServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
//        super(devopsConfig, objectMapper);
//    }
//
//    @Override
//    public FirewallCloudPlatformModel page(String getParams) {
//        FirewallPageResult pageResult = super.sendGet(URL_VERSION + "/firewalls" + getParams, FirewallPageResult.class);
//        if (Objects.isNull(pageResult) || Objects.isNull(pageResult.getData())){
//            return null;
//        }
//        CloudPlatformPageResult<Firewall> data = pageResult.getData();
//        List<Firewall> list = data.getList();
//        FirewallCloudPlatformModel model = new FirewallCloudPlatformModel();
//        model.setFirewalls(CollectionUtils.isEmpty(list) ? Lists.newArrayList() : list);
//        model.setTotal(data.getTotal());
//        model.setCode(pageResult.getCode());
//        return model;
//    }
//
//
//}
