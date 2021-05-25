package cn.cyberpeace.mp.datasandtable.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class SandTableConfig {

    @Value("${mp.datasandtable.group-url}")
    private String groupUrl;
}
