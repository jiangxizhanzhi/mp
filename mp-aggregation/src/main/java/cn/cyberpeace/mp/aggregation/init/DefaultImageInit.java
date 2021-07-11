package cn.cyberpeace.mp.aggregation.init;

import cn.cyberpeace.mp.aggregation.constant.AggregationSingleton;
import cn.cyberpeace.mp.auth.constant.DefaultImgDirEnum;
import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.hutool.core.io.FileUtil;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultImageInit implements ApplicationRunner {

    @Autowired
    private BaseAppConfig baseAppConfig;

    @Override
    public void run(ApplicationArguments args) {
        log.info("DefaultImageInit init value is {}", baseAppConfig.isRunInit());
        
        if (!baseAppConfig.isRunInit()) {
            return;
        }

        TaskCommon.executeTask(() -> {
            String startWithFile = "file:";
            String staticLocation = AggregationSingleton.aggregationConfig.getStaticLocation();
            log.info("DefaultImageInit staticLocation value is {}", staticLocation);
            if (staticLocation.startsWith(startWithFile)) {
                staticLocation = staticLocation.substring(5).trim();
            }

            for (DefaultImgDirEnum imgDirEnum : DefaultImgDirEnum.values()) {
                if (StringUtils.isNotBlank(imgDirEnum.getEndPathName())) {
                    log.info("DefaultImageInit imgDirEnum value is {}", imgDirEnum.getEndPathName());
                    String path = staticLocation.concat(imgDirEnum.getEndPathName());
                    log.info("DefaultImageInit path value is {}", path);
                    if (FileUtil.isDirectory(path)) {
                        LocalFileMappingCommon.syncDir(imgDirEnum.getGroup(), path);
                    }
                }
            }
        });
    }

}
