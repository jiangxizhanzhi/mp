package cn.cyberpeace.mp.sceneplugincloud.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Configuration
@ConfigurationProperties(prefix = "mp.sceneplugincloud")
@Data
public class CloudAppConfig {

    @Value("${mp.sceneplugincloud.cpcloud_server:http://127.0.0.1:10009}")
    private String cpcloudServer;

    @Value("${mp.sceneplugincloud.cpcloud_server_timeout:600000}")
    private Integer cpcloudServerTimeout;

    @Value("${mp.sceneplugincloud.cpcloud_server_timeout:MP_SCENE}")
    private String cpcloudResourceGroup;

    public List<String> getCidrPool() {
        List<String> cidrPool = new ArrayList<>();
        IntStream.range(172, 173).forEach(cidrA -> IntStream.range(0, 255)
                .forEach(cidrB -> IntStream.range(0, 255)
                        .forEach(cidrC -> cidrPool.add(String.format("%d.%d.%d.0/24", cidrA, cidrB, cidrC)))));
        return cidrPool;
    }
}
