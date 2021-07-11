package cn.cyberpeace.mp.common.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.bo.OptionBO;
import cn.cyberpeace.mp.common.bo.OptionModelBO;
import cn.cyberpeace.mp.common.model.OptionModel;
import cn.cyberpeace.mp.common.repository.OptionMapper;
import cn.cyberpeace.mp.common.request.queryparam.OptionQueryParams;
import cn.cyberpeace.mp.common.service.OptionService;
import cn.cyberpeace.mp.common.service.fields.OptionFields;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;


@Service
@Slf4j
public class OptionServiceImpl extends BaseModelServiceImpl<OptionModel, OptionMapper, OptionFields, OptionQueryParams>
        implements OptionService {

    @Autowired
    private OptionMapper optionMapper;

    @Autowired
    public OptionServiceImpl(OptionMapper optionMapper, OptionFields optionFields) {
        setModelFields(optionFields);
        setModelMapper(optionMapper);
    }

    
    @Override
    public void loadListFilterCondition(HumanCondition humanCondition, OptionQueryParams queryParams) {
        if (StringUtils.isNotEmpty(queryParams.getType())) {
            humanCondition.filterEqual("type", queryParams.getType());
        }
        if (StringUtils.isNotEmpty(queryParams.getZhName())) {
            humanCondition.filterContains("zhName", queryParams.getZhName());
        }
    }

    
    private List<OptionBO> getOptionsByType(String type, boolean intFlag) {
        List<OptionModel> models = findByType(type);
        Function<String, ?> function = null;

        if (intFlag) {
            function = Integer::parseInt;
        } else {
            function = s -> s;
        }

        return flatModels2NestedBos(models, function);
    }

    @Override
    public List<OptionModel> findByType(String type) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(OptionModel::getType, type);
        condition.orderBy(OptionModel::getSort);
        List<OptionModel> optionModels = getModelMapper().list(condition);
        return optionModels;
    }

    @Override
    public ServiceResult<List<OptionBO>> getStrOptions(String type) {
        return new ServiceResult<>(getOptionsByType(type, false)).asResult();
    }


    @Override
    public ServiceResult<List<OptionBO>> getIntOptions(String type) {
        return new ServiceResult<>(getOptionsByType(type, true)).asResult();
    }

    
    @Override
    public ListObjServiceResult<OptionModelBO> page(OptionQueryParams queryParams) {
        log.info("OptionServiceImpl.page begin. queryParams:{}", queryParams);
        ListObjServiceResult<OptionModel> listObjServiceResult = this.list(queryParams);
        Locale locale = LocaleContextHolder.getLocale();
        List<OptionModelBO> boList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(listObjServiceResult.getSource().getObjs())) {
            for (OptionModel optionModel : listObjServiceResult.getSource().getObjs()) {
                OptionModelBO optionModelBO = new OptionModelBO();
                BeanUtils.copyProperties(optionModel, optionModelBO);
                if (Objects.equals(Locale.US, locale)) {
                    optionModelBO.setName(optionModel.getEnName());
                } else {
                    optionModelBO.setName(optionModel.getZhName());
                }
                boList.add(optionModelBO);
            }
        }
        ListObjServiceResult<OptionModelBO> result = new ListObjServiceResult<>(new ListObjResult<>(boList, listObjServiceResult.getSource().getTotal())).asResult();
        log.info("OptionServiceImpl.page end. result:{}", result);
        return result;
    }

    
    @Override
    public void addOption(OptionModelBO optionModelBO) {

    }

    
    @Override
    public void editOption(OptionModelBO optionModelBO) {

    }

    
    @Override
    public void deleteOptions(List<String> ids) {

    }

    
    @Override
    public OptionModelBO findById(String id) {
        return null;
    }

    
    @Override
    public List<OptionModel> findByIds(List<String> ids) {
        return null;
    }

    
    @Override
    public Integer getMaxCode(String type) {
        log.info("SysOptionServiceImpl.getMaxCode begin. type:{}", type);
        Integer result = optionMapper.getMaxCode(type);
        result = result == null ? 0 : result;
        log.info("SysOptionServiceImpl.getMaxCode end. result:{}", result);
        return result;
    }

    
    @Override
    public Integer getMaxSort(String type) {
        log.info("SysOptionServiceImpl.getMaxSort begin. type:{}", type);
        Integer result = optionMapper.getMaxSort(type);
        result = result == null ? 0 : result;
        log.info("SysOptionServiceImpl.getMaxSort end. result:{}", result);
        return result;
    }

    private List<OptionBO> flatModels2NestedBos(List<OptionModel> models, Function<String, ?> function) {
        Locale locale = LocaleContextHolder.getLocale();
        List<OptionBO> rootBos = new ArrayList<>();
        Map<String, OptionBO> optionBoMap = new HashMap<>(models.size());
        List<OptionModel> nextLevelModels = new ArrayList<>();
        for (OptionModel model : models) {
            OptionBO optionBO = new OptionBO();
            optionBO.fromModel(model, locale, function);
            optionBoMap.put(model.getCode(), optionBO);
            if (StringUtils.isBlank(model.getParentCode())) {
                rootBos.add(optionBO);
            } else {
                nextLevelModels.add(model);
            }
        }

        for (OptionModel model : nextLevelModels) {
            String parentCode = model.getParentCode();
            OptionBO parent = optionBoMap.get(parentCode);
            if (parent == null) {
                log.warn("The node(code = {}) has wrong parent code!", model.getCode());
                continue;
            }
            if (parent.getChildren() == null) {
                parent.setChildren(new ArrayList<>());
            }
            OptionBO optionBO = optionBoMap.get(model.getCode());
            parent.getChildren().add(optionBO);
        }
        return rootBos;
    }
}
