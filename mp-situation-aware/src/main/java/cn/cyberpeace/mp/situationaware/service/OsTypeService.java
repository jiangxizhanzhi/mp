package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.situationaware.model.OsTypeModel;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
public interface OsTypeService extends BaseModelService<OsTypeModel, QueryParams> {

    /**
     * 查询所有类型的操作系统
     * @return
     */
    List<OsTypeModel> findAll();
}
