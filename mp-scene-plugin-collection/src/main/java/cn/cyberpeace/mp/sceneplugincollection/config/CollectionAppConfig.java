package cn.cyberpeace.mp.sceneplugincollection.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "mp.sceneplugincollection")
@Data
public class CollectionAppConfig {

    @Value("${mp.sceneplugincollection.create_pool_size:10}")
    private Integer initiatePoolSize;

    @Value("${mp.sceneplugincollection.create_hold_size:200000}")
    private Integer initiateHoldSize;

    @Value("${mp.sceneplugincollection.create_pool_size:10}")
    private Integer createPoolSize;

    @Value("${mp.sceneplugincollection.create_hold_size:200000}")
    private Integer createHoldSize;

    @Value("${mp.sceneplugincollection.delete_pool_size:10}")
    private Integer deletePoolSize;

    @Value("${mp.sceneplugincollection.delete_hold_size:200000}")
    private Integer deleteHoldSize;

}
