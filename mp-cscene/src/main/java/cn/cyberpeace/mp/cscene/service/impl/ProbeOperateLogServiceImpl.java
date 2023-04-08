package cn.cyberpeace.mp.cscene.service.impl;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.cscene.bo.ProbeOperateBO;
import cn.cyberpeace.mp.cscene.model.ProbeOperateLogModel;
import cn.cyberpeace.mp.cscene.repository.ProbeOperateLogMapper;
import cn.cyberpeace.mp.cscene.service.ProbeOperateLogService;
import cn.cyberpeace.mp.cscene.service.filed.ProbeOperateLogFields;
import cn.cyberpeace.mp.cscene.service.param.ProbeOperateQueryParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @BelongsPackage: cn.cyberpeace.mp.cscene.service.impl
 * @Author: hhuang
 * @CreateTime: 2023/04/07  16:05
 * @Description: 探针开关操作记录service
 * @Version: 1.0
 */
@Service
public class ProbeOperateLogServiceImpl extends BaseModelServiceImpl<ProbeOperateLogModel, ProbeOperateLogMapper, ProbeOperateLogFields, QueryParams>
    implements ProbeOperateLogService {


    @Autowired
    public ProbeOperateLogServiceImpl(ProbeOperateLogMapper probeOperateLogMapper, ProbeOperateLogFields probeOperateLogFields) {
        setModelMapper(probeOperateLogMapper);
        setModelFields(probeOperateLogFields);
    }

    @Override
    public List<ProbeOperateBO> queryOperateProbe(ProbeOperateQueryParams probeOperateQueryParams) {
        //必须存在场景id
        if(StringUtils.isEmpty(probeOperateQueryParams.getScene())){
            return new ArrayList<>();
        }
        //构建查询条件
        HumanCondition condition = new HumanCondition().filterEqual(ProbeOperateLogModel::getScene, probeOperateQueryParams.getScene());
        if(StringUtils.isNotEmpty(probeOperateQueryParams.getType())){
            condition.filterEqual(ProbeOperateLogModel::getType,probeOperateQueryParams.getType());
        }
        return getModelMapper().list(condition,ProbeOperateBO.class);
    }

    @Override
    public boolean operateProbeBatchInsert(List<ProbeOperateBO> probeOperateBOList) {
        //对象转换
        List<ProbeOperateLogModel> probeOperateLogModelList = probeOperateBOList.stream().map(probeOperateBO -> {
            return ProbeOperateLogModel.builder().scene(probeOperateBO.getScene()).type(probeOperateBO.getType()).operate(probeOperateBO.getOperate()).build();
        }).collect(Collectors.toList());
        //批量插入
        BaseModelMapper.BulkResult<ProbeOperateLogModel> probeOperateLogModelBulkResult =
            getModelMapper().bulkCreate(probeOperateLogModelList);
        return probeOperateLogModelBulkResult.getSuccessCount() > 0;
    }
}
