package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.situationaware.bo.AttackClassificationBO;
import cn.cyberpeace.mp.situationaware.model.FlowClassificationModel;
import cn.cyberpeace.mp.situationaware.repository.FlowClassificationsMapper;
import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FlowClassificationsService {

    private FlowClassificationsMapper flowClassificationsMapper;

    private static final String cache_prefix_model = "situation:";

    private Cache<String, Object> lfuCache = null;

    private static final Long TIME_OUT = 2 * 60 * 60 * 1000L;

    @PostConstruct
    public void init(){
        lfuCache = CacheUtil.newLFUCache(100);
    }


    @Autowired
    public FlowClassificationsService(FlowClassificationsMapper flowClassificationsMapper) {
        this.flowClassificationsMapper = flowClassificationsMapper;
    }

    /**
     * 查询所有攻击类型
     * @return
     */
    public List<FlowClassificationModel> findAll(){
        String key = cache_prefix_model + "flow:" + ServletUtils.getAcceptLanguage();
        Object cache = lfuCache.get(key);
        if (Objects.nonNull(cache)){
            return (List<FlowClassificationModel>)cache;
        }
        List<FlowClassificationModel> modelList = flowClassificationsMapper.findAll();
        lfuCache.put(key,modelList);
        return modelList;
    }

    /**
     * 查询所有攻击类型
     * @return
     */
    public Map<String,FlowClassificationModel> findAllCategoryDicMap(){
        List<FlowClassificationModel> categoryDicList = this.findAll();
        if (CollectionUtils.isEmpty(categoryDicList)){
            return Maps.newHashMap();
        }
        return categoryDicList.stream().collect(Collectors.toMap(FlowClassificationModel::getCategoryEn, Function.identity()));
    }

    /**
     * 删除所有
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(){
         flowClassificationsMapper.deleteAll();
    }

    /**
     * 批量保存
     * @param classificationBO
     * @return
     */
    @Transactional
    public Integer batchSave(AttackClassificationBO classificationBO){
        List<AttackClassificationBO.Classification> classifications = classificationBO.getClassifications();
        List<FlowClassificationModel> categoryDicList = classifications.stream().map(classification -> {
            FlowClassificationModel categoryDic = new FlowClassificationModel();
            categoryDic.setId(TextUtils.uuid());
            categoryDic.setName(classification.getName());
            categoryDic.setCategoryEn(classification.getEn_desc());
            categoryDic.setCategoryZh(classification.getZh_desc());
            categoryDic.setSeverity(classification.getSeverity());
            return categoryDic;
        }).collect(Collectors.toList());
        return flowClassificationsMapper.batchInsert(categoryDicList);
    }
}
