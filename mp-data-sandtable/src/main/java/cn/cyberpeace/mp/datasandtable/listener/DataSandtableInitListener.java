package cn.cyberpeace.mp.datasandtable.listener;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.datasandtable.constant.DataSandtableConstant;
import cn.cyberpeace.mp.datasandtable.repository.WazuhRepository;
import cn.cyberpeace.mp.datasandtable.model.GroupModel;
import cn.cyberpeace.mp.datasandtable.service.NetworkTrafficGroupService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 启动完后生命周期进行
 */
@Component
@Slf4j
public class DataSandtableInitListener implements ApplicationListener<ApplicationReadyEvent> {



    @Autowired
    private NetworkTrafficGroupService service;

    @Autowired
    private WazuhRepository wazuhRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("prepare init actions for mp system");

        //初始化
        DataSandtableConstant.groupIdSessionUrlCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.DAYS)
            .build(new CacheLoader<String, String>() {
                @Override public String load(String key)  {
                    GroupModel groupModel = service.queryGroupById(key);
                    return groupModel.getUrl();
                }
            });

        //定时更新token初始化
        DataSandtableConstant.wazuhToken = CacheBuilder.newBuilder()
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
    }
}
