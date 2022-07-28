package cn.cyberpeace.mp.common.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.common.bo.DropdownBO;
import cn.cyberpeace.mp.common.fields.DropdownFields;
import cn.cyberpeace.mp.common.model.DropdownModel;
import cn.cyberpeace.mp.common.repository.DropdownMapper;
import cn.cyberpeace.mp.common.service.DropdownService;
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
public class DropdownServiceImpl extends BaseModelServiceImpl<DropdownModel, DropdownMapper, DropdownFields, QueryParams>
        implements DropdownService {

    public DropdownServiceImpl(DropdownMapper dropdownMapper) {
        setModelMapper(dropdownMapper);
    }

    @Override
    public List<DropdownModel> findByType(int type) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual("type", type);
        condition.orderBy("sort");
        return getModelMapper().list(condition);
    }

    @Override
    public List<DropdownBO> getOptionsByType(int type) {
        Locale locale = LocaleContextHolder.getLocale();
        List<DropdownModel> models = findByType(type);
        List<DropdownBO> boList = new ArrayList<>();
        for (DropdownModel model : models) {
            boList.add(model.toBO(locale));
        }
        return boList;
    }

//    @Override
//    public List<DropdownModel> findOptionsByType(int type) {
//
//    }


}
