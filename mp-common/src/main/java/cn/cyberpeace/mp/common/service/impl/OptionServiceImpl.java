package cn.cyberpeace.mp.common.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.bo.OptionBO;
import cn.cyberpeace.mp.common.fields.OptionFields;
import cn.cyberpeace.mp.common.model.OptionModel;
import cn.cyberpeace.mp.common.repository.OptionMapper;
import cn.cyberpeace.mp.common.service.OptionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

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
        return getModelMapper().list(condition);
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
