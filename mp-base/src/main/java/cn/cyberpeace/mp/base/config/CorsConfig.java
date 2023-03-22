package cn.cyberpeace.mp.base.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * @BelongsPackage: cn.cyberpeace.mp.base.config
 * @Author: hhuang
 * @CreateTime: 2023/03/22  14:06
 * @Description: 支持跨域
 * @Version: 1.0
 */
@Configuration
@Slf4j
public class CorsConfig {
    @Bean
    public CorsWebFilter corsFilter() {
        log.info("cors global init.");
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*");
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");

        // 4）有效时间
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
