package cn.cyberpeace.mp.flow.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description
 * @Author shgang
 * @Date 2022/8/31
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mp.flow")
public class FlowConfig {

    /**
     * trex镜像名称
     */
    @Value("${mp.flow.trex-image:tgn-trex}")
    private String trexImage;

    /**
     * 云平台地址
     */
    private String cpUrl;

    @Value("${mp.flow.trex_cpu:4}")
    private Integer trexCpu;

    @Value("${mp.flow.trex_ram:8194}")
    private Integer trexRam;

    @Value("${mp.flow.trex_disk:40}")
    private Integer trexDisk;

}
