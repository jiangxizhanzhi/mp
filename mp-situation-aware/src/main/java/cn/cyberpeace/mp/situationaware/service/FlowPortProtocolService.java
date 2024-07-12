 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowPortProtocolService.java.
 // File size 2656.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.situationaware.model.FlowPortProtocolModel;
import cn.cyberpeace.mp.situationaware.repository.FlowPortProtocolMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class FlowPortProtocolService {

    private FlowPortProtocolMapper flowPortProtocolMapper;

    private static final String cache_prefix_model = "situation:";

    private Cache<String, Object> lfuCache = null;

    /**
     * init
     */
    @PostConstruct
    public void init() {
        lfuCache = CacheBuilder.newBuilder().maximumSize(100).build();
    }

    /**
     * FlowPortProtocolService
     *
     * @param flowPortProtocolMapper
     */
    @Autowired
    public FlowPortProtocolService(FlowPortProtocolMapper flowPortProtocolMapper) {
        this.flowPortProtocolMapper = flowPortProtocolMapper;
    }

    /**
     * 查询所有端口与协议
     *
     * @return
     */
    public List<FlowPortProtocolModel> findAll(){
        String key = cache_prefix_model + "flow:port:protocol:" + ServletUtils.getAcceptLanguage();
        Object cache = lfuCache.getIfPresent(key);
        if (Objects.nonNull(cache)){
            return (List<FlowPortProtocolModel>)cache;
        }
        List<FlowPortProtocolModel> modelList = flowPortProtocolMapper.list(new HumanCondition());
        lfuCache.put(key,modelList);
        return modelList;
    }

    /**
     * 转成Map格式
     *
     * @return
     */
    public Map<String, String> convertToMap() {
        List<FlowPortProtocolModel> modelList = this.findAll();
        if (CollectionUtils.isEmpty(modelList)) {
            return Maps.newHashMap();
        }
        Map<String, String> map = new HashMap<>();
        for (FlowPortProtocolModel flowPortProtocolModel : modelList) {
            if (map.put(flowPortProtocolModel.getPort(), flowPortProtocolModel.getProtocol()) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        return map;
    }
}
