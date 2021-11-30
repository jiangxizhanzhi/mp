package cn.cyberpeace.mp.auth.listener;

import cn.cyberpeace.mp.auth.service.AvatarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/9/6 23:09
 */
@Slf4j
@Component
public class AvatarInitListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private AvatarService avatarService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // todo 将默认头像图片转到前端
//        new Thread(new AvatarInitThread(avatarService)).start();
//        log.info("success!");
    }

}
