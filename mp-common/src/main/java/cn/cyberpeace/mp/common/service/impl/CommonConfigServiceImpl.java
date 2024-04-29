package cn.cyberpeace.mp.common.service.impl;

import cn.cyberpeace.mp.base.bo.MpSysUrlConfig;
import cn.cyberpeace.mp.common.repository.CommonConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommonConfigServiceImpl {

    @Autowired
    public CommonConfigMapper commonConfigMapper;

    public MpSysUrlConfig getUrlByKey(String code) {
        return commonConfigMapper.getUrlByKey(code);
    }
}

