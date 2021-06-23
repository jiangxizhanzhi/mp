package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.devops.cluster.config.OpenStackConfig;
import cn.cyberpeace.mp.devops.cluster.model.BaseOpenStackModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 基础对接云平台接口层
 * @param <T>
 */
@Slf4j
@Service
public class BaseOpenStackServiceImpl<T extends BaseOpenStackModel> {

    protected OpenStackConfig openStackConfig;

    protected ObjectMapper objectMapper;

    @Autowired
    public BaseOpenStackServiceImpl(OpenStackConfig openStackConfig,ObjectMapper objectMapper) {
        this.openStackConfig = openStackConfig;
        this.objectMapper = objectMapper;
    }

    /**
     * 分页查询信息
     */
    protected T page(String api,Class<T> valueType){
        T model = null;
        String result = "";
        try {
            result = HttpUtils.sendGet(openStackConfig.getUrl().concat(api));
            model = objectMapper.readValue(result,valueType);
        } catch (IOException e) {
            log.info("=====分页查询信息失败,api接口名称：{},对应返回结果信息：{},报错信息：{}",api,result,e.getMessage());
        }
        return model;
    }
}
