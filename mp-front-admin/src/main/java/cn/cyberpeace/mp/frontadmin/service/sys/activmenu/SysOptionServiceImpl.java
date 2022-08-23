package cn.cyberpeace.mp.frontadmin.service.sys.activmenu;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.bo.OptionBO;
import cn.cyberpeace.mp.common.fields.OptionFields;
import cn.cyberpeace.mp.common.model.OptionModel;
import cn.cyberpeace.mp.common.repository.OptionMapper;
import cn.cyberpeace.mp.common.service.OptionService;
import cn.cyberpeace.mp.frontadmin.dto.option.OptionMetaModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/8/15 15:57
 */
@Slf4j
@Service
public class SysOptionServiceImpl extends BaseModelServiceImpl<OptionModel, OptionMapper, OptionFields, QueryParams>
        implements OptionService {


    @Autowired
    public SysOptionServiceImpl(OptionMapper optionMapper) {
        setModelMapper(optionMapper);
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

    private void filterByVisibility(List<OptionModel> optionModels) {
        Iterator<OptionModel> iter = optionModels.iterator();
        while (iter.hasNext()) {
            String metaStr = iter.next().getMeta();
            OptionMetaModel metaModel = JsonCommon.parse(metaStr, OptionMetaModel.class);
            if (Objects.equals(OptionMetaModel.OPTION_VISIBLE_NO, String.valueOf(metaModel.getVisible()))) {
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
}
