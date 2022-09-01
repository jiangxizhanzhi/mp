package cn.cyberpeace.mp.situationaware.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SituationAwareConfig situationAwareConfig;

    @PostConstruct
    public void init() {
        String datePattern = situationAwareConfig.getEsDatePattern();
        esDateTimeFormatter = DateTimeFormatter.ofPattern(datePattern);
    }

}
