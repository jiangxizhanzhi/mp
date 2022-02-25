package cn.cyberpeace.mp.situationaware.model.wazuh.syscollector;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/24 14:16
 */
@Data
public class Scan {

    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private LocalDateTime time;

}
