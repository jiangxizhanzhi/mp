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

import java.util.*;

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

    private List<OptionBO> flatModels2NestedBos(List<OptionModel> models, Locale locale) {
        List<OptionBO> boList = new ArrayList<>();
        // parentMap 记录已经加入到嵌套结构中的节点，key为code，value为OptionBO对象
        // 用于子级节点根据parentCode查找父级节点
        Map<String, OptionBO> parentMap = new HashMap<>(models.size());
        // codeSet 记录所有节点的code，用于校验parentCode合法性
        Set<String> codeSet = new HashSet<>();
        List<OptionModel> nextLevelModels = new ArrayList<>();
        for (OptionModel model : models) {
            String code = model.getCode();

            OptionBO optionBO = new OptionBO();
            optionBO.fromModel(model, locale);
            codeSet.add(code);
            if (model.getParentCode() == null) {
                boList.add(optionBO);
                parentMap.put(code, optionBO);
            } else {
                nextLevelModels.add(model);
            }
        }
        while (nextLevelModels.size() > 0) {
            List<OptionModel> tmpList = new ArrayList<>();
            for (OptionModel model : nextLevelModels) {
                if (!codeSet.contains(model.getParentCode())) {
                    throw new RuntimeException("没有上一级选项");
                }

                if (parentMap.containsKey(model.getParentCode())) {
                    OptionBO parent = parentMap.get(model.getParentCode());
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    OptionBO optionBO = new OptionBO();
                    optionBO.fromModel(model, locale);
                    parent.getChildren().add(optionBO);
                    parentMap.put(model.getCode(), optionBO);
                } else {
                    tmpList.add(model);
                }
            }
            // 校验是否成环
            if (nextLevelModels.size() == tmpList.size()) {
                throw new RuntimeException("节点中存在环");
            }
            nextLevelModels = new ArrayList<>(tmpList);
            tmpList.clear();
        }
        return boList;
    }

    @Override
    public List<OptionBO> getOptionsByType(String type) {
        Locale locale = LocaleContextHolder.getLocale();
        List<OptionModel> models = findByType(type);
        return flatModels2NestedBos(models, locale);
    }
}
