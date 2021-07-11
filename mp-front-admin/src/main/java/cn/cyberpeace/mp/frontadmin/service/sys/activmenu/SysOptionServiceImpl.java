package cn.cyberpeace.mp.frontadmin.service.sys.activmenu;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
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
import cn.cyberpeace.mp.frontadmin.constant.AdminErrorCode;
import cn.cyberpeace.mp.frontadmin.dto.option.OptionMetaModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;


@Slf4j
@Service
public class SysOptionServiceImpl extends BaseModelServiceImpl<OptionModel, OptionMapper, OptionFields, OptionQueryParams>
        implements OptionService {

    @Autowired
    private OptionMapper optionMapper;

    @Autowired
    public SysOptionServiceImpl(OptionMapper optionMapper, OptionFields optionFields) {
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

    
    private List<OptionBO> getOptionsByType(String type, boolean intFlag, boolean filterByMetaVisible) {
        List<OptionModel> models = findByType(type);
        Function<String, ?> function = null;

        if (filterByMetaVisible) {
            filterByVisibility(models);
        }

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
        filterByVisibility(optionModels);
        return optionModels;
    }

    @Override
    public ServiceResult<List<OptionBO>> getStrOptions(String type) {
        return new ServiceResult<>(getOptionsByType(type, false, true)).asResult();
    }


    @Override
    public ServiceResult<List<OptionBO>> getIntOptions(String type) {
        return new ServiceResult<>(getOptionsByType(type, true, true)).asResult();
    }

    
    @Override
    public ListObjServiceResult<OptionModelBO> page(OptionQueryParams queryParams) {
        log.info("SysOptionServiceImpl.page begin. queryParams:{}", queryParams);
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
        log.info("SysOptionServiceImpl.page end. result:{}", result);
        return result;
    }

    
    @Override
    public void addOption(OptionModelBO optionModelBO) {
        log.info("SysOptionServiceImpl.addOption begin. optionModelBO:{}", optionModelBO);
        if (StringUtils.isEmpty(optionModelBO.getName())) {
            throw new MpRuntimeException(AdminErrorCode.PARAMS_ERROR);
        }
        if (checkNameExist(optionModelBO, false)) {
            throw new MpRuntimeException(AdminErrorCode.OPTION_NAME_EXIST);
        }
        OptionModel optionModel = new OptionModel();
        BeanUtils.copyProperties(optionModelBO, optionModel);
        Locale locale = LocaleContextHolder.getLocale();
        if (Objects.equals(Locale.US, locale)) {
            optionModel.setEnName(optionModelBO.getName());
        } else {
            optionModel.setZhName(optionModelBO.getName());
        }
        getModelMapper().create(optionModel);
        log.info("SysOptionServiceImpl.addOption end.");
    }

    
    @Override
    public void editOption(OptionModelBO optionModelBO) {
        log.info("SysOptionServiceImpl.editOption begin. optionModelBO:{}", optionModelBO);
        if (StringUtils.isEmpty(optionModelBO.getName()) || StringUtils.isEmpty(optionModelBO.getId())) {
            throw new MpRuntimeException(AdminErrorCode.PARAMS_ERROR);
        }
        if (checkNameExist(optionModelBO, true)) {
            throw new MpRuntimeException(AdminErrorCode.OPTION_NAME_EXIST);
        }
        OptionModel optionModel = new OptionModel();
        BeanUtils.copyProperties(optionModelBO, optionModel);
        Locale locale = LocaleContextHolder.getLocale();
        if (Objects.equals(Locale.US, locale)) {
            optionModel.setEnName(optionModelBO.getName());
        } else {
            optionModel.setZhName(optionModelBO.getName());
        }
        getModelMapper().update(optionModel);
        log.info("SysOptionServiceImpl.editOption end.");
    }

    
    @Override
    public void deleteOptions(List<String> ids) {
        log.info("SysOptionServiceImpl.deleteOptions begin. ids:{}", ids);
        this.bulkDelete(ids.toArray(new String[ids.size()]));
        log.info("SysOptionServiceImpl.deleteOptions end.");
    }

    
    @Override
    public OptionModelBO findById(String id) {
        log.info("SysOptionServiceImpl.findById begin. id:{}", id);
        OptionModelBO optionModelBO = new OptionModelBO();
        OptionModel optionModel = getModelMapper().get(id);
        BeanUtils.copyProperties(optionModel, optionModelBO);
        Locale locale = LocaleContextHolder.getLocale();
        if (Objects.equals(Locale.US, locale)) {
            optionModelBO.setName(optionModel.getEnName());
        } else {
            optionModelBO.setName(optionModel.getZhName());
        }
        log.info("SysOptionServiceImpl.deleteOptions end. optionModelBO：{}", optionModelBO);
        return optionModelBO;
    }

    
    @Override
    public List<OptionModel> findByIds(List<String> ids) {
        log.info("SysOptionServiceImpl.findByIds begin. ids:{}", ids);
        HumanCondition condition = new HumanCondition();
        condition.filterIn(OptionModel::getId, ids.toArray(new String[ids.size()]));
        condition.orderBy(OptionModel::getSort);
        List<OptionModel> optionModels = getModelMapper().list(condition);
        log.info("SysOptionServiceImpl.findByIds end. optionModels:{}", optionModels);
        return optionModels;
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

    private void filterByVisibility(List<OptionModel> optionModels) {
        Iterator<OptionModel> iter = optionModels.iterator();
        while (iter.hasNext()) {
            String metaStr = iter.next().getMeta();
            OptionMetaModel metaModel = JsonCommon.parse(metaStr, OptionMetaModel.class);
            if (Objects.equals(OptionMetaModel.OPTION_VISIBLE_NO, metaModel.getVisible())) {
                iter.remove();
            }
        }
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

    
    private boolean checkNameExist(OptionModelBO optionModelBO, boolean exceptOwn) {
        log.info("SysOptionServiceImpl.checkNameExist begin. optionModelBO:{}, exceptOwn:{}", optionModelBO, exceptOwn);
        if (StringUtils.isEmpty(optionModelBO.getType()) || StringUtils.isEmpty(optionModelBO.getName())) {
            return false;
        }
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(OptionModel::getType, optionModelBO.getType());
        Locale locale = LocaleContextHolder.getLocale();
        if (Objects.equals(Locale.US, locale)) {
            condition.filterEqual(OptionModel::getEnName, optionModelBO.getName());
        } else {
            condition.filterEqual(OptionModel::getZhName, optionModelBO.getName());
        }
        if (exceptOwn) {
            condition.filterNotEqual(OptionModel::getId, optionModelBO.getId());
        }
        List<OptionModel> optionModels = getModelMapper().list(condition);
        boolean result = CollectionUtils.isNotEmpty(optionModels);
        log.info("SysOptionServiceImpl.checkNameExist end. result:{}", result);
        return result;
    }
}
