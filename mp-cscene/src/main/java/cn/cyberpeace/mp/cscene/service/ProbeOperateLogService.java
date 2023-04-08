package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.cscene.bo.ProbeOperateBO;
import cn.cyberpeace.mp.cscene.model.ProbeOperateLogModel;
import cn.cyberpeace.mp.cscene.service.param.ProbeOperateQueryParams;

import java.util.List;

public interface ProbeOperateLogService extends BaseModelService<ProbeOperateLogModel, QueryParams> {



    /**
     * @description: 查询探针操作记录
     * @param probeOperateQueryParams
     * @return List<ProbeOperateBO>
     */
    List<ProbeOperateBO> queryOperateProbe(ProbeOperateQueryParams probeOperateQueryParams);

    /**
     * @description: 探针开关操作记录批量新增
     * @param list
     * @return boolean
     */
    boolean operateProbeBatchInsert(List<ProbeOperateBO> list);


}
