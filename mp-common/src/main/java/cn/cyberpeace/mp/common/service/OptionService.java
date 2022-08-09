package cn.cyberpeace.mp.common.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.bo.OptionBO;
import cn.cyberpeace.mp.common.model.OptionModel;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/28
 */
public interface OptionService extends BaseModelService<OptionModel, QueryParams> {

    /**
     * 根据类型查询选项
     *
     * @param type
     * @return
     */
    List<OptionModel> findByType(String type);

    /**
     * 根据类型将选项转成 OptionBO
     *
     * @param type
     * @param intFlag
     * @return
     */
    ServiceResult<List<OptionBO>> getOptionsByType(String type, boolean intFlag);
}
