package cn.cyberpeace.mp.front.admin.service.sys;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.front.admin.model.SysConfigModel;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public interface SysConfigService extends BaseModelService<SysConfigModel, QueryParams> {

    List<SysConfigModel> listAll();

    SysConfigModel getByKeyName(String keyName);

    boolean updateByKeyName(SysConfigModel sysConfigModel);

    boolean batchUpdateByKeyname(List<SysConfigModel> configs);

    /**
     * 是否需要审核 true（1）:需要 false（0）:不需要
     * @return
     */
    boolean isNewUserNeedAudit();

    void publishConfigUpdateEvent(List<SysConfigModel> configs);

}
