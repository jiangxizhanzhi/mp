package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.situationaware.model.FlowPortProtocolModel;
import cn.cyberpeace.mp.situationaware.repository.FlowPortProtocolMapper;
import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FlowPortProtocolService {

    private FlowPortProtocolMapper flowPortProtocolMapper;

    private static final String cache_prefix_model = "situation:";

    private Cache<String, Object> lfuCache = null;

    @PostConstruct
    public void init(){
        lfuCache = CacheUtil.newLFUCache(100);
    }


    @Autowired
    public FlowPortProtocolService(FlowPortProtocolMapper flowPortProtocolMapper) {
        this.flowPortProtocolMapper = flowPortProtocolMapper;
    }

    
    public List<FlowPortProtocolModel> findAll(){
        String key = cache_prefix_model + "flow:port:protocol:" + ServletUtils.getAcceptLanguage();
        Object cache = lfuCache.get(key);
        if (Objects.nonNull(cache)){
            return (List<FlowPortProtocolModel>)cache;
        }
        List<FlowPortProtocolModel> modelList = flowPortProtocolMapper.list(new HumanCondition());
        lfuCache.put(key,modelList);
        return modelList;
    }

    
    public Map<String,String> convertToMap(){
        List<FlowPortProtocolModel> modelList = this.findAll();
        if (CollectionUtils.isEmpty(modelList)){
            return Maps.newHashMap();
        }
        return modelList.stream().collect(Collectors.toMap(FlowPortProtocolModel::getPort,FlowPortProtocolModel::getProtocol));
    }
}
