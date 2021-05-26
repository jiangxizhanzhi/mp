package cn.cyberpeace.mp.datasandtable.listener;

import cn.cyberpeace.mp.datasandtable.model.GroupModel;
import cn.cyberpeace.mp.datasandtable.service.GroupService;
import cn.cyberpeace.mp.datasandtable.service.SessionService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;
import lombok.Data;
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
@Data
public class DataSandtableInitListener implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * 分组id url 分组
     */
    private LoadingCache<String, String> groupIdSessionUrlCache;

    /**
     * 分组id
     */
    private LoadingCache<String, String> groupIdPortCache;

    @Autowired
    private GroupService service;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("prepare init actions for mp system");

        //初始化
        groupIdSessionUrlCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.DAYS)
            .build(new CacheLoader<String, String>() {
                @Override public String load(String key)  {
                    GroupModel groupModel = service.queryGroupById(key);
                    return groupModel.getUrl();
                }
            });
    }
}
