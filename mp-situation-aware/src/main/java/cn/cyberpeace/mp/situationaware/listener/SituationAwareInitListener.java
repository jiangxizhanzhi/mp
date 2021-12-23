package cn.cyberpeace.mp.situationaware.listener;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import cn.cyberpeace.mp.situationaware.repository.WazuhRepository;
import cn.cyberpeace.mp.situationaware.model.GroupModel;
import cn.cyberpeace.mp.situationaware.service.NetworkTrafficGroupService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 启动完后生命周期进行
 */
@Component
@Slf4j
public class SituationAwareInitListener implements ApplicationListener<ApplicationReadyEvent> {



    @Autowired
    private NetworkTrafficGroupService service;

    @Autowired
    private WazuhRepository wazuhRepository;

    @Value("${mp.situationaware.arkime.cpcloud-container-create-url}")
    private String cpCloudUrl;

    private static String ARKIMENETWORKNAME = "oj_ext";

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("prepare init actions for mp system");

        //初始化
        SituationalAwareConstant.groupIdSessionUrlCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.DAYS)
            .build(new CacheLoader<String, String>() {
                @Override public String load(String key)  {
                    GroupModel groupModel = service.queryGroupById(key);
                    return groupModel.getUrl();
                }
            });

        //定时更新token初始化
        SituationalAwareConstant.wazuhToken = CacheBuilder.newBuilder()
            .maximumSize(1)
            .expireAfterWrite(30, TimeUnit.DAYS)
            .build(new CacheLoader<String, String>() {
                @Override public String load(String key)  {
                    String token;
                    try {
                        token = wazuhRepository.queryWazuhToken();
                    } catch (IOException e) {
                        log.error("查询wazuhToken失败 : " + e);
                        throw new MpRuntimeException("查询wazuhToken失败");
                    }
                    return token;
                }
            });

        initArkimeNetwork();
    }

    /**
     * 初始化arkime网络配置
     */
    public void initArkimeNetwork() {
        log.info("init arkime network");
        try {
            String result = HttpUtils.sendGet(cpCloudUrl + "/networks?fetch_all=true");
            log.info("init arkime network result {}", result);
            JSONArray networkList = (JSONArray) JSONPath.eval(JSON.parse(result), "$.data.list");
            networkList.stream().filter(json -> json instanceof JSONObject).map(JSONObject.class::cast).filter(json -> ARKIMENETWORKNAME.equals(json.get("name"))).findFirst().ifPresent(network ->{
                SituationalAwareConstant.ArkimeNetworkId = (String) network.get("id");
                log.info("ArkimeNetworkId {}", SituationalAwareConstant.ArkimeNetworkId);
            });
        } catch (Exception e) {
            log.info("init arkime network error {}", ExceptionUtils.getMessage(e));
        }
    }
}
