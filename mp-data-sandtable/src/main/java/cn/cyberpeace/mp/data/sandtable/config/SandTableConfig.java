package cn.cyberpeace.mp.data.sandtable.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class SandTableConfig {

    @Value("${mp.data.sandtable.group-url}")
    private String groupUrl;
}
