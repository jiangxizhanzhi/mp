package cn.cyberpeace.mp.data.sandtable.service;

import cn.cyberpeace.mp.base.exception.MqRuntimeException;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.data.sandtable.config.SandTableConfig;
import cn.cyberpeace.mp.data.sandtable.model.Group;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * 流量依赖的组的CRUD
 */
@Service
@Slf4j
public class GroupServiceImpl {

    @Autowired
    private SandTableConfig sandTableConfig;

    /**
     * 查询底层所有场景数据
     * @return≠
     */
    public List<Group> queryAllScene(){
        if (ObjectUtils.isEmpty(sandTableConfig.getGroupUrl())) {
            throw new MqRuntimeException("底层场景服务url不存在，请配置");
        }
        String sceneListString;
        try {
            sceneListString = HttpUtils.sendGet(sandTableConfig.getGroupUrl());
        } catch (IOException e) {
            log.error("查询所有场景错误:"+ e);
            throw new MqRuntimeException("查询场景错误:"+ e);
        }

        return null;
    }
}
