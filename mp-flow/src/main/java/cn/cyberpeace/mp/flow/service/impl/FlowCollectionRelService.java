 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowCollectionRelServiceImpl.java.
 // File size 3083.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.model.FlowCollectionRelModel;
import cn.cyberpeace.mp.flow.repository.FlowCollectionRelMapper;
import cn.cyberpeace.mp.flow.request.DelFlowCollectionRelRequest;
import cn.cyberpeace.mp.flow.request.queryparam.FlowCollectionRelQueryParams;
import cn.cyberpeace.mp.flow.service.field.FlowCollectionRelFields;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName FlowCollectionRelServiceImpl
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelServiceImpl
 */
@Service
@Slf4j
public class FlowCollectionRelService extends BaseModelServiceImpl<FlowCollectionRelModel, FlowCollectionRelMapper, FlowCollectionRelFields, FlowCollectionRelQueryParams> {

    public FlowCollectionRelService(FlowCollectionRelMapper stuMapper, FlowCollectionRelFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    /**
     * 补充list查询条件
     * @param humanCondition
     * @param queryParams
     */
    @Override
    public void loadListFilterCondition(HumanCondition humanCondition, FlowCollectionRelQueryParams queryParams) {
        if (StringUtils.isNotBlank(queryParams.getCollectionId())) {
            humanCondition.filterEqual("collectionId", queryParams.getCollectionId());
        }
    }

    /**
     * 根据集合id查询关联信息
     * @param collectionIds
     * @return
     */
    public List<FlowCollectionRelModel> selectByCollectionIds(List<String> collectionIds) {
        return getModelMapper().selectByCollectionIds(collectionIds);
    }

    /**
     * 删除关联关系
     * @param delRequest
     * @return
     */
    public ServiceResult<Integer> delCollectionRel(DelFlowCollectionRelRequest delRequest) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual("collectionId", delRequest.getCollectionId());
        condition.filterIn("flowId", delRequest.getFlowIds().toArray(new String[0]));
        int count = getModelMapper().filterDelete(condition);
        return ResultCommon.success(count);
    }

    /**
     * 根据collectionId删除
     * @param collectionId
     * @return
     */
    public Integer delByCollectionId(String collectionId) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowCollectionRelModel::getCollectionId, collectionId);
        return getModelMapper().filterDelete(condition);
    }


}
