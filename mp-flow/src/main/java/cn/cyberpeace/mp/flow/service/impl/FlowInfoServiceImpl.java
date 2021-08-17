package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowInfoMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskMapper;
import cn.cyberpeace.mp.flow.request.BatchDeleteFlowInfoRequest;
import cn.cyberpeace.mp.flow.service.FlowInfoService;
import cn.cyberpeace.mp.flow.service.field.FlowInfoFields;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @Description: 流量service
 * @Autho qiaokang
 * @Date：2021/6/10 10:31
 */
@Service
@Slf4j
public class FlowInfoServiceImpl extends BaseModelServiceImpl<FlowInfoModel, FlowInfoMapper, FlowInfoFields, QueryParams> implements FlowInfoService {

    public FlowInfoServiceImpl(FlowInfoMapper stuMapper, FlowInfoFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    @Autowired
    private FlowTaskMapper flowTaskMapper;

    /**
     * 删除流量信息
     *
     * @param id
     * @return
     */
    public ServiceResult<Boolean> deleteFlowInfo(String id) {
        // 判断流量任务是否关联该流量信息
        List<FlowTaskModel> flowTaskList = flowTaskMapper.listByFlowIds(Collections.singletonList(id));
        if (CollectionUtils.isNotEmpty(flowTaskList)) {
            return ServiceResult.fail("Can not delete flow task related flow info");
        }

        return delete(id);
    }

    /**
     * 批量删除流量信息
     *
     * @param request
     * @return
     */
    public ServiceResult<Integer> batchDeleteFlowInfo(BatchDeleteFlowInfoRequest request) {
        // 判断流量任务是否关联该流量信息
        List<FlowTaskModel> flowTaskList = flowTaskMapper.listByFlowIds(request.getIdList());
        if (CollectionUtils.isNotEmpty(flowTaskList)) {
            log.info("flow info related flow task can not delete: {}", flowTaskList);
            return ServiceResult.fail("Can not delete flow task related flow info");
        }

        String[] delIds = request.getIdList().toArray(new String[0]);
        return bulkDelete(delIds);
    }

}
