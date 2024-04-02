package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.aggregation.constant.AggregationSingleton;
import cn.cyberpeace.mp.auth.constant.DefaultImgDirEnum;
import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.job.base.init.MpInitiate;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
public class DefaultImageInitJob {

    @Autowired
    private BaseAppConfig baseAppConfig;

    @MpInitiate(description = "初始化默认头像和靶标文件,将文件上传并获取地址", id = "default_image_init")
    public void run() {
        log.info("DefaultImageInit init value is {}", baseAppConfig.isRunInit());
        // 本地开发环境不运行资源的更新和删除
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
                    Path dirPath = Paths.get(path);
                    if (Files.isDirectory(dirPath)) {
                        LocalFileMappingCommon.syncDir(imgDirEnum.getGroup(), path);
                    }
                }
            }
        });
    }

}
