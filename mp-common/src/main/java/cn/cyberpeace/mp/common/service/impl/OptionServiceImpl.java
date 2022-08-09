package cn.cyberpeace.mp.common.service.impl;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.bo.OptionBO;
import cn.cyberpeace.mp.common.constant.CommonConstant;
import cn.cyberpeace.mp.common.fields.OptionFields;
import cn.cyberpeace.mp.common.model.OptionModel;
import cn.cyberpeace.mp.common.repository.OptionMapper;
import cn.cyberpeace.mp.common.service.OptionService;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/28
 */
@Service
@Slf4j
public class OptionServiceImpl extends BaseModelServiceImpl<OptionModel, OptionMapper, OptionFields, QueryParams>
        implements OptionService {

    @Autowired
    public OptionServiceImpl(OptionMapper optionMapper) {
        setModelMapper(optionMapper);
    }

    @Override
    public ServiceResult<List<OptionBO>> getOptionsByType(String type) {
        List<OptionModel> models = findByType(type);
        return new ServiceResult(flatModels2NestedBos(models)).asResult();
    }

    public List<OptionModel> findByType(String type) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(OptionModel::getType, type);
        condition.orderBy(OptionModel::getSort);
        List<OptionModel> optionModels = getModelMapper().list(condition);
        filterByVisibility(optionModels);
        return optionModels;
    }


    public void resetMeta(Map<String, String> activeItemMap) {
        if (CollectionUtil.isEmpty(activeItemMap)) {
            return;
        }

        List<String> activedKeys = activeItemMap.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), "1")).map(Map.Entry::getKey).collect(Collectors.toList());
        HumanCondition condition = new HumanCondition();
        condition.setLimit(9999);
        List<OptionModel> optionModels = getModelMapper().list(condition);

        List<OptionModel> toUpdateList = new ArrayList<>();
        LocalDateTime updatime = LocalDateTime.now();

        for (OptionModel optionModel : optionModels) {
            String metaStr = optionModel.getMeta();
            OptionModel.MetaModel metaModel = JsonCommon.parse(metaStr, OptionModel.MetaModel.class);
            if (StringUtils.isEmpty(metaModel.getActivKeys())) {
                continue;
            }
            String[] metaActiveKeyArr = metaModel.getActivKeys().split(",");
            List<String> metaActiveKeyList = Arrays.asList(metaActiveKeyArr);
            boolean isNoAuth = metaActiveKeyList.stream().filter(key -> Objects.equals(key, CommonConstant.OPTION_ACTIVE_KEY_NO_AUTH)).findAny().isPresent();
            if (isNoAuth) {
                continue;
            }

            if (CollectionUtil.isEmpty(activedKeys)) {
                metaModel.setVisible(0);
            } else {
                boolean isVisible = metaActiveKeyList.stream().filter(key -> activedKeys.contains(key)).findAny().isPresent();
                if (isVisible) {
                    metaModel.setVisible(1);
                } else {
                    metaModel.setVisible(0);
                }
            }

            OptionModel toUpdateOptionModel = new OptionModel();
            BeanUtils.copyProperties(optionModel, toUpdateOptionModel);
            String toUpdateMetaStr = JsonCommon.stringify(metaModel);
            toUpdateOptionModel.setMeta(toUpdateMetaStr);
            toUpdateOptionModel.setUpdateTime(updatime);
            toUpdateList.add(toUpdateOptionModel);
        }

        if (CollectionUtil.isNotEmpty(toUpdateList)) {
            getModelMapper().bulkUpdate(toUpdateList);
        }
    }

    private void filterByVisibility(List<OptionModel> optionModels) {
        Iterator<OptionModel> iter = optionModels.iterator();
        while (iter.hasNext()) {
            String metaStr = iter.next().getMeta();
            OptionModel.MetaModel metaModel = JsonCommon.parse(metaStr, OptionModel.MetaModel.class);
            if (Objects.equals(0, metaModel.getVisible())) {
                iter.remove();
            }
        }
    }

    private List<OptionBO> flatModels2NestedBos(List<OptionModel> models) {
        Locale locale = LocaleContextHolder.getLocale();
        List<OptionBO> rootBos = new ArrayList<>();

        Map<String, OptionBO> optionBoMap = new HashMap<>(models.size());
        List<OptionModel> nextLevelModels = new ArrayList<>();
        for (OptionModel model : models) {
            OptionBO optionBO = new OptionBO();
            optionBO.fromModel(model, locale);
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
