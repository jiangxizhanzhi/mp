package cn.cyberpeace.mp.auth.listener;

import cn.cyberpeace.mp.auth.model.AvatarModel;
import cn.cyberpeace.mp.auth.service.AvatarService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

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
        checkAndInitDefaultAvatars();
    }

    private void checkAndInitDefaultAvatars() {
        // todo 将默认头像图片转到前端
        try {
            // 检查是否需要自动上传默认头像
            log.info("check if need to upload default avatar!");
            List<AvatarModel> avatarModels = avatarService.listAll();
            if (CollectionUtil.isNotEmpty(avatarModels) && avatarModels.size() == 9) {
                log.info("no need to upload default avatar!");
                return;
            } else {
                log.info("uploading default avatar manually!");
                List<AvatarModel> modelList = avatarService.uploadDefaultAvatars();
                avatarService.clearAndReUploadAvatars(modelList);
            }
        } catch (Exception e) {
            log.error("初始化默认头像异常！\n{}", ExceptionUtil.stacktraceToOneLineString(e));
        }
    }
}
