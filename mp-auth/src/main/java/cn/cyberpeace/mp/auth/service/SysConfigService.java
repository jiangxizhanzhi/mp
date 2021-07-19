package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.SysConfigModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.util.List;

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


//    /**
//     * 系统初始化（加载默认头像，默认下载模板等）
//     * @return
//     */
//    boolean initDefaultAvatars();

    void checkAndInitDefaultAvatars();

}
