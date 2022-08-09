package cn.cyberpeace.mp.common.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.bo.OptionBO;
import cn.cyberpeace.mp.common.model.OptionModel;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/28
 */
public interface OptionService extends BaseModelService<OptionModel, QueryParams> {

    /**
     * 根据类型将选项转成 OptionBO
     * @param type
     * @return
     */
    ServiceResult<List<OptionBO>> getOptionsByType(String type);

    /**
     * 根据激活配置项重置所有类型可见属性
     * @param activeItemMap
     */
    void resetMeta(Map<String, String> activeItemMap);
}
