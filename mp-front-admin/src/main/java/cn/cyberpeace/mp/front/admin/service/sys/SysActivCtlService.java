package cn.cyberpeace.mp.front.admin.service.sys;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.front.admin.model.SysActivCtlModel;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/15 14:18
 */
public interface SysActivCtlService extends BaseModelService<SysActivCtlModel, QueryParams> {

    SysActivCtlModel selectValid();

    SysActivCtlModel selectUnused();

    /**
     * set all records' status = 0 except ${exceptedId}
     * @param exceptedId
     * @return
     */
    void invalidOtherExceptOne(String exceptedId);

}
