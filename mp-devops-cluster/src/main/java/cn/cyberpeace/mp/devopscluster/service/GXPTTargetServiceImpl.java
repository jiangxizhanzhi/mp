package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.common.service.impl.CommonConfigServiceImpl;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.GXPTTargetMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.net.HttpHeaders.ACCEPT_LANGUAGE;

/**
 * 共性平台方法调用
 */
@Slf4j
@Service
public class GXPTTargetServiceImpl {

    @Autowired
    private GXPTTargetMapper gxpttargetMapper;

    @Autowired
    private CommonConfigServiceImpl commonConfigService;

    // 共性平台新增靶标
    public Map<String, Object> addTarget(DeviceTargetPO target) {
        String url = commonConfigService.getUrlByKey("addTarget").getServiceUrl();

        Map<String, Object> formMap = new HashMap();
        formMap.put("cjPlanId", target.getId());
        formMap.put("name", target.getName());
        formMap.put("pictureData", "");
        formMap.put("ip", "");
        formMap.put("mac", "");
        formMap.put("gateway", "");
        formMap.put("netmask", "");
        formMap.put("position", "");
        formMap.put("os", "");
        formMap.put("manufacturer", "");
        formMap.put("kernel", "");
        formMap.put("type", "");
        formMap.put("status", "");
        formMap.put("cpu", "");
        formMap.put("hardDisk", "");
        formMap.put("ram", "");
        formMap.put("createDate", "");
        formMap.put("sessionId", "");

        //请求头设置
        HashMap<String, String> headersMap = new HashMap<>();
        headersMap.put(ACCEPT_LANGUAGE, ServletUtils.getAcceptLanguage());

        String result = null;

        try {
            result = HttpUtils.sendHttpPost(url, formMap, headersMap);
        } catch (Exception e) {
            return null;
        }
        Map<String, Object> dataMap;
        dataMap = JsonUtils.parseMap(result);
        return dataMap;
    }

    public void deleteTarget(List<String> imageIds) {
        String url = commonConfigService.getUrlByKey("deleteTarget").getServiceUrl();

        //根据uuids查询共性平台的ids
        List<String> ids = gxpttargetMapper.findIdsByUuids(imageIds);
        for(String id : ids){
            Map<String, Object> formMap = new HashMap();
            formMap.put("ids", id);
            //请求头设置
            HashMap<String, String> headersMap = new HashMap<>();
            headersMap.put(ACCEPT_LANGUAGE, ServletUtils.getAcceptLanguage());
            try {
                String result = HttpUtils.sendHttpPost(url, formMap, headersMap);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
