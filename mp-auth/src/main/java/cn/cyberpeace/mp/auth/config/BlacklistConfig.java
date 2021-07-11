package cn.cyberpeace.mp.auth.config;

import cn.cyberpeace.mp.auth.model.BlacklistModel;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPatternParser;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@Component
public class BlacklistConfig implements ApplicationRunner {
    @Resource
    private BaseAppConfig appConfig;

    @Resource
    private BlacklistModel blacklistModel;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String dataPath = appConfig.getResourcesDir() + File.separator + "auth" + File.separator + "blacklist.txt";
        if (new File(dataPath).exists()) {
            List<String> items;
            try {
                items = FileUtil.readUtf8Lines(dataPath);
            } catch (Exception ex) {
                log.error("read blacklist.txt error", ex);
                return;
            }
            if (items == null || items.isEmpty()) {
                log.info("no blacklist pattern, ignore");
                return;
            }

            String context = appConfig.getServerProperties().getServlet().getContextPath();

            
            Set<BlacklistModel.AccessItem> patterns = new HashSet<>();
            items.forEach(item -> {
                String[] splits = item.trim().split(" +");
                BlacklistModel.AccessItem accessItem = BlacklistModel.AccessItem.builder().build();
                if (splits.length > 1) {
                    accessItem.setMethod(splits[0].toUpperCase());
                    accessItem.setPattern(new PathPatternParser().parse(context+splits[1]));
                } else {
                    accessItem.setPattern(new PathPatternParser().parse(context+item));
                }
                patterns.add(accessItem);
            });
            blacklistModel.setPatterns(patterns);

            
            if(!patterns.isEmpty()){
                blacklistModel.setEnabled(true);
            }
        }
    }
}
