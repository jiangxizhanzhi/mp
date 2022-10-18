package cn.cyberpeace.mp.collection.core.config;

import lombok.Data;

import java.time.format.DateTimeFormatter;

/**
 * es时间戳配置类
 * @author hewei
 */
@Data
public class EsDateFormatConfig {

    public static DateTimeFormatter esDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public void update(String datePattern) {
        esDateTimeFormatter = DateTimeFormatter.ofPattern(datePattern);
    }
}
