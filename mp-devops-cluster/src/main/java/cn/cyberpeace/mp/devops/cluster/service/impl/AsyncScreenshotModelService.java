package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.enums.FdfsGroupEnum;
import cn.cyberpeace.mp.base.util.fdfsclient.FastDFSClient;
import cn.cyberpeace.mp.devops.cluster.model.ScreenshotModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 截屏文件异步操作类
 * @Author qiaokang
 * @Date：2021/9/13 16:17
 */
@Service
@Slf4j
public class AsyncScreenshotModelService {

    @Autowired
    private FastDFSClient fastDFSClient;

    /**
     * 轮询删除截屏文件
     * @param screenshotModels
     */
    @Async
    public void batchDeleteScreenshot(List<ScreenshotModel> screenshotModels) {
        try {
            if (CollectionUtils.isNotEmpty(screenshotModels)) {
                screenshotModels.forEach(model -> fastDFSClient.deleteFile(model.getFile(), FdfsGroupEnum.DEFAULT.toString()));
            }
        } catch (Exception e) {
            log.error("delete screenshot file error", e);
        }
    }

}
