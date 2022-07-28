package cn.cyberpeace.mp.common.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.common.bo.DropdownBO;
import cn.cyberpeace.mp.common.model.DropdownModel;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/28
 */
public interface DropdownService extends BaseModelService<DropdownModel, QueryParams> {

    /**
     * 从数据库根据类型查询所有下拉选项
     * @param type
     * @return
     */
    List<DropdownModel> findByType(int type);

    /**
     * 根据类型将选项转成 DropdownBO
     * @param type
     * @return
     */
    List<DropdownBO> getOptionsByType(int type);
}
