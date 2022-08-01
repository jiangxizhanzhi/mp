package cn.cyberpeace.mp.common.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.common.bo.OptionBO;
import cn.cyberpeace.mp.common.fields.OptionFields;
import cn.cyberpeace.mp.common.model.OptionModel;
import cn.cyberpeace.mp.common.repository.OptionMapper;
import cn.cyberpeace.mp.common.service.OptionService;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/28
 */
@Service
public class OptionServiceImpl extends BaseModelServiceImpl<OptionModel, OptionMapper, OptionFields, QueryParams>
        implements OptionService {

    public OptionServiceImpl(OptionMapper optionMapper) {
        setModelMapper(optionMapper);
    }

    @Override
    public List<OptionModel> findByType(String type) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual("type", type);
        condition.orderBy("sort");
        return getModelMapper().list(condition);
    }

    @Override
    public List<OptionBO> getOptionsByType(String type) {
        Locale locale = LocaleContextHolder.getLocale();
        List<OptionModel> models = findByType(type);
        List<OptionBO> boList = new ArrayList<>();
        for (OptionModel model : models) {
            OptionBO optionBO = new OptionBO();
            optionBO.fromModel(model, locale);
            boList.add(optionBO);
        }
        return boList;
    }
}
