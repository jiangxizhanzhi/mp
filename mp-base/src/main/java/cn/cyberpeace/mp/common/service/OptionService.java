package cn.cyberpeace.mp.common.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.bo.OptionBO;
import cn.cyberpeace.mp.common.bo.OptionModelBO;
import cn.cyberpeace.mp.common.model.OptionModel;
import cn.cyberpeace.mp.common.request.queryparam.OptionQueryParams;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/28
 */
public interface OptionService extends BaseModelService<OptionModel, OptionQueryParams> {

    /**
     * 根据类型查询选项
     *
     * @param type
     * @return
     */
    List<OptionModel> findByType(String type);

    /**
     * 获取 bo 的 code 字段为字符串类型
     *
     * @param type
     * @return
     */
    ServiceResult<List<OptionBO>> getStrOptions(String type);


    /**
     * 获取 bo 的 code 字段为 int 类型
     *
     * @param type
     * @return
     */
    ServiceResult<List<OptionBO>> getIntOptions(String type);

    /**
     * 分页查询OptionModel列表
     * @param queryParams
     * @return
     */
    ListObjServiceResult<OptionModelBO> page(OptionQueryParams queryParams);

    /**
     * 新增选项
     * @param optionModelBO
     */
    void addOption(OptionModelBO optionModelBO);

    /**
     * 编辑选项
     * @param optionModelBO
     */
    void editOption(OptionModelBO optionModelBO);

    /**
     * 删除选项
     * @param ids
     */
    void deleteOptions(List<String> ids);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    OptionModelBO findById(String id);

    /**
     * 根据ids查询列表
     * @param ids
     * @return
     */
    List<OptionModel> findByIds(List<String> ids);

    /**
     * 根据type类型获取最大的code值（只对数字类型的code有效）
     * @param type
     * @return
     */
    Integer getMaxCode(String type);

    /**
     * 根据type类型获取最大的sort值
     * @param type
     * @return
     */
    Integer getMaxSort(String type);

}
