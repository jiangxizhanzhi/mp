package cn.cyberpeace.mp.situationaware.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;

/**
 * 对接云平台配置类
 * @author wanggang
 */
@Configuration
@Data
public class EsDateFormatConfig {

    public static DateTimeFormatter esDateTimeFormatter = null;

    @Value("${mp.situationaware.es.date_pattern:yyyy-MM-dd'T'HH:mm:ss.SSSZ}")
    private String datePattern;

    @PostConstruct
    public void init() {
        esDateTimeFormatter = DateTimeFormatter.ofPattern(datePattern);
    }

}
