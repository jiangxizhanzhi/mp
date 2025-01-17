 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BlacklistConfig.java.
 // File size 2748.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
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

 /**
  * 黑名单配置
  *
  * @author litian
  * 接口访问黑名单配置
  * 应用启动后读取${home}/.mp/resources/blacklist.txt，作为黑名单，禁止用户访问@date 2025/01/17
  */
 @Slf4j
@Component
public class BlacklistConfig implements ApplicationRunner {
     /**
      * 应用程序配置
      */
     @Resource
    private BaseAppConfig appConfig;

     /**
      * 黑名单模式
      */
     @Resource
    private BlacklistModel blacklistModel;

     /**
      * 运行
      *
      * @param args arg游戏
      * @throws Exception 异常
      */
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

            // 将配置的访问路径转换为Pattern对象存储，提高过滤匹配性能
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

            // 如果黑名单列表中有内容，则默认开启黑名单控制
            if(!patterns.isEmpty()){
                blacklistModel.setEnabled(true);
            }
        }
    }
}
