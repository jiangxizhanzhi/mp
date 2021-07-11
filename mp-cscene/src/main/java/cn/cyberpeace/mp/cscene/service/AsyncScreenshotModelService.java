package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.cscene.model.ScreenshotModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class AsyncScreenshotModelService {

    
    @Async
    public void batchDeleteScreenshot(List<ScreenshotModel> screenshotModels) {
        try {
            if (CollectionUtils.isNotEmpty(screenshotModels)) {
                screenshotModels.forEach(model -> FsCommon.delete(model.getFile()));
            }
        } catch (Exception e) {
            log.error("delete screenshot file error", e);
        }
    }

}
