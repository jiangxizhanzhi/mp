package cn.cyberpeace.mp.log.service.impl;

import cn.cyberpeace.mp.log.config.LogConfig;
import cn.cyberpeace.mp.log.model.BaseModel;
import cn.hutool.http.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class BaseCpHttpService extends BaseHttpService {

    @Autowired
    private LogConfig logConfig;

    
    @Override
    protected <T extends BaseModel> T sendPost(String api, String body, Class<T> valueType) {
        return super.sendPost(logConfig.getCpUrl().concat(api), body, valueType);
    }

    
    @Override
    protected HttpResponse downLoad(String api, String body) {
        return super.downLoad(logConfig.getCpUrl().concat(api),body);
    }
}
