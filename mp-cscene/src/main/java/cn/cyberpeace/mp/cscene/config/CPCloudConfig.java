package cn.cyberpeace.mp.cscene.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/6/23 10:35
 */
@Data
@Configuration
public class CPCloudConfig {

    @Value("${ip.cp}")
    private String cpIP;

}
