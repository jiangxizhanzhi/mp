 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BlacklistModel.java.
 // File size 1032.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPattern;

import java.util.List;
import java.util.Set;

/**
 * @ClassName BlacklistModel
 * @Description 访问黑名单模型数据
 * @Author litian
 * @Date 2023年09月25 11:22
 * @Version 1.0.0
 */
@Component
@Data
public class BlacklistModel {
    /**
     * 启用开关，默认关闭
     */
    private boolean enabled;

    /**
     * 禁止访问列表，这里配置的内容为requestURI去除上下文的部分
     * 如访问地址：GET http://localhost:8080/context/path/abc?param=value
     * 则其访问URI为：GET /path/abc
     * 支持Pattern通配符
     */
    private Set<AccessItem> patterns;

    @Data
    @Builder
    public static class AccessItem {
        private String method;
        private PathPattern pattern;
    }
}
