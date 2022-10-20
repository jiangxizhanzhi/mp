package cn.cyberpeace.mp.collectionapi.localservice;


import cn.cyberpeace.mp.base.annotation.LocalCallFunc;
import cn.cyberpeace.mp.base.annotation.LocalCallServer;
import cn.cyberpeace.mp.collection.core.es.CollectionClient;
import cn.cyberpeace.mp.collection.core.wazuh.WazuhClientProxy;
import cn.cyberpeace.mp.collectionapi.config.GroupConfig;
import cn.cyberpeace.mp.collectionapi.init.CollectionApiInit;
import cn.cyberpeace.mp.collectionapi.util.GroupUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.File;

@Component
@LocalCallServer
@Slf4j
public class CollectionApiService {
    @Value("${mp.situationaware.wazuh.username}")
    private String wazuhUsername;

    @Value("${mp.situationaware.wazuh.password}")
    private String wazuhPassword;

    @Resource
    private CollectionClient collectionClient;

    private static Integer day = 3;

    /**
     * 手动触发更新GroupConfig
     * @param filePath 包含Group配置的json文件路径
     */
    @LocalCallFunc
    public void updateGroupConfig(String filePath) {
        try {
            File file = ResourceUtils.getFile(filePath);
            String content = FileUtil.readString(file, "utf8");
            GroupConfig groupConfig = JSON.parseObject(content, GroupConfig.class);
            // 更新groupConfig
            GroupUtil.updateGroupConfig(groupConfig);
            CollectionApiInit.init(groupConfig);
            // 更新esClient
//            RestHighLevelClientImpl.updateClientMap();
            //  更新wazuhClient
            WazuhClientProxy.updateClientMap(wazuhUsername, wazuhPassword);
        } catch (Exception e) {
            log.error("update group config error:{}", ExceptionUtils.getStackTrace(e));
        }
    }
}
