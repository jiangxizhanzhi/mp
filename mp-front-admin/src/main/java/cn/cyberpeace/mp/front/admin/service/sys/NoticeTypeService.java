package cn.cyberpeace.mp.front.admin.service.sys;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.front.admin.model.NoticeTypeModel;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
public interface NoticeTypeService extends BaseModelService<NoticeTypeModel, QueryParams> {
    /**
     * 查询所有系统公告类型
     * @return
     */
    List<NoticeTypeModel> findAll();
}
