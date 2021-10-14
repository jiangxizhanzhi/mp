package cn.cyberpeace.mp.scenepluginflow.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "mp.scenepluginflow")
@Data
public class FlowAppConfig {

    @Value("${mp.scenepluginflow.create_pool_size:10}")
    private Integer createPoolSize;

    @Value("${mp.scenepluginflow.create_hold_size:200000}")
    private Integer createHoldSize;

    @Value("${mp.scenepluginflow.delete_pool_size:10}")
    private Integer deletePoolSize;

    @Value("${mp.scenepluginflow.delete_hold_size:200000}")
    private Integer deleteHoldSize;

}
