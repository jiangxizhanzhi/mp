package cn.cyberpeace.mp.auth.thread;

import cn.cyberpeace.mp.auth.model.AvatarModel;
import cn.cyberpeace.mp.auth.service.AvatarService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/10/20 14:29
 */
@Slf4j
public class AvatarInitThread implements Runnable {

    private AvatarService avatarService;

    public AvatarInitThread(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @Override
    public void run() {
        // 检查是否需要自动上传默认头像
        log.info("check if need to upload default avatar!");
        try {
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
