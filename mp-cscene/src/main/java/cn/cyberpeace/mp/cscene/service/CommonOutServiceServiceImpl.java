package cn.cyberpeace.mp.cscene.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommonOutServiceServiceImpl {

    // 共性平台改造地址
    @Value("${project.setting.gxpt-service-url:http://ip:port}")
    private String GXPT_SERVICE_URL;

    public String getGXPTServiceUrl() {
        return GXPT_SERVICE_URL;
    }
}

