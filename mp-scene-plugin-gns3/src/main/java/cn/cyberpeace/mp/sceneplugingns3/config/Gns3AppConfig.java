package cn.cyberpeace.mp.sceneplugingns3.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Slf4j
@Configuration
@ConfigurationProperties(prefix = "mp.sceneplugingns3")
@Data
public class Gns3AppConfig {

    @Value("${mp.sceneplugingns3.gns3_server:http://127.0.0.1:8088}")
    private String gns3Server;

    @Value("${mp.sceneplugingns3.create_pool_size:10}")
    private Integer createPoolSize;

    @Value("${mp.sceneplugingns3.create_hold_size:200000}")
    private Integer createHoldSize;

    @Value("${mp.sceneplugingns3.initiate_pool_size:10}")
    private Integer initiatePoolSize;

    @Value("${mp.sceneplugingns3.initiate_hold_size:200000}")
    private Integer initiateHoldSize;

    @Value("${mp.sceneplugingns3.delete_pool_size:10}")
    private Integer deletePoolSize;

    @Value("${mp.sceneplugingns3.delete_hold_size:200000}")
    private Integer deleteHoldSize;

    @Value("#{'${mp.sceneplugingns3.gns3_image_uuids:854f7c24-060f-4dbd-b771-68eab207a071}'.split(',')}")
    private List<String> gns3ImageUuids;

}
