package cn.cyberpeace.mp.frontadmin.service.sys.activmenu;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.bo.OptionBO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/8/15 15:57
 */
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

    /**
     * 补充list查询条件
     * @param humanCondition
     * @param queryParams
     */
    @Override
    public void loadListFilterCondition(HumanCondition humanCondition, OptionQueryParams queryParams) {
        if (StringUtils.isNotEmpty(queryParams.getType())) {
            humanCondition.filterEqual("type", queryParams.getType());
        }
        if (StringUtils.isNotEmpty(queryParams.getZhName())) {
            humanCondition.filterContains("zhName", queryParams.getZhName());
        }
    }

    /**
     *
     * @param type
     * @param intFlag bo中code字段是否转为int类型
     * @return
     */
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

    /**
     * 分页查询OptionModel列表
     * @param queryParams
     * @return
     */
    @Override
    public ListObjServiceResult<OptionModel> page(OptionQueryParams queryParams) {
        log.info("SysOptionServiceImpl.page begin. queryParams:{}", queryParams);
        ListObjServiceResult<OptionModel> listObjServiceResult = list(queryParams);
        log.info("SysOptionServiceImpl.page end. listObjServiceResult:{}", listObjServiceResult);
        return listObjServiceResult.asResult();
    }

    /**
     * 新增选项
     * @param optionModel
     */
    @Override
    public void addOption(OptionModel optionModel) {
        log.info("SysOptionServiceImpl.addOption begin. optionModel:{}", optionModel);
        if (StringUtils.isEmpty(optionModel.getZhName())) {
            throw new MpRuntimeException(AdminErrorCode.PARAMS_ERROR);
        }
        if (checkZhNameExist(optionModel, false)) {
            throw new MpRuntimeException(AdminErrorCode.OPTION_NAME_EXIST);
        }
        this.create(optionModel);
        log.info("SysOptionServiceImpl.addOption end.");
    }

    /**
     * 编辑选项
     * @param optionModel
     */
    @Override
    public void editOption(OptionModel optionModel) {
        log.info("SysOptionServiceImpl.editOption begin. optionModel:{}", optionModel);
        if (StringUtils.isEmpty(optionModel.getZhName()) || StringUtils.isEmpty(optionModel.getId())) {
            throw new MpRuntimeException(AdminErrorCode.PARAMS_ERROR);
        }
        if (checkZhNameExist(optionModel, true)) {
            throw new MpRuntimeException(AdminErrorCode.OPTION_NAME_EXIST);
        }
        this.update(optionModel);
        log.info("SysOptionServiceImpl.editOption end.");
    }

    /**
     * 删除选项
     * @param ids
     */
    @Override
    public void deleteOptions(List<String> ids) {
        log.info("SysOptionServiceImpl.deleteOptions begin. ids:{}", ids);
        this.bulkDelete(ids.toArray(new String[ids.size()]));
        log.info("SysOptionServiceImpl.deleteOptions end.");
    }

    /**
     * 根据ids查询列表
     * @param ids
     * @return
     */
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

    /**
     * 根据type类型获取最大的code值（只对数字类型的code有效）
     * @param type
     * @return
     */
    @Override
    public Integer getMaxCode(String type) {
        log.info("SysOptionServiceImpl.getMaxCode begin. type:{}", type);
        Integer result = optionMapper.getMaxCode(type);
        result = result == null ? 0 : result;
        log.info("SysOptionServiceImpl.getMaxCode end. result:{}", result);
        return result;
    }

    /**
     * 根据type类型获取最大的sort值
     * @param type
     * @return
     */
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

    /**
     * 检查是否存在重名
     * @param optionModel
     * @param exceptOwn
     * @return
     */
    private boolean checkZhNameExist(OptionModel optionModel, boolean exceptOwn) {
        log.info("SysOptionServiceImpl.checkZhNameExist begin. optionModel:{}, exceptOwn:{}", optionModel, exceptOwn);
        if (StringUtils.isEmpty(optionModel.getType()) || StringUtils.isEmpty(optionModel.getZhName())) {
            return false;
        }
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(OptionModel::getType, optionModel.getType());
        condition.filterEqual(OptionModel::getZhName, optionModel.getZhName());
        if (exceptOwn) {
            condition.filterNotEqual(OptionModel::getId, optionModel.getId());
        }
        List<OptionModel> optionModels = getModelMapper().list(condition);
        boolean result = CollectionUtils.isNotEmpty(optionModels);
        log.info("SysOptionServiceImpl.checkZhNameExist end. result:{}", result);
        return result;
    }
}
