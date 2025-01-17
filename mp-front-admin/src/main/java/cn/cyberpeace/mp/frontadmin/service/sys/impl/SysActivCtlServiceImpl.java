 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysActivCtlServiceImpl.java.
 // File size 2283.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service.sys.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysActivCtlModel;
import cn.cyberpeace.mp.frontadmin.repository.SysActivCtlMapper;
import cn.cyberpeace.mp.frontadmin.service.fields.SysActivCtlFields;
import cn.cyberpeace.mp.frontadmin.service.sys.SysActivCtlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

 /**
  * sys活性细胞毒性t淋巴细胞服务impl
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Service
@Slf4j
public class SysActivCtlServiceImpl extends BaseModelServiceImpl<SysActivCtlModel, SysActivCtlMapper, SysActivCtlFields, QueryParams>
        implements SysActivCtlService {

     /**
      * sys活性细胞毒性t淋巴细胞服务impl
      *
      * @param modelMapper 模型映射器
      * @param modelFields 模型领域
      */
     @Autowired
    public SysActivCtlServiceImpl(SysActivCtlMapper modelMapper, SysActivCtlFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

     /**
      * 选择未使用
      *
      * @return {@link SysActivCtlModel }
      */
     @Override
    public SysActivCtlModel selectUnused() {
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(SysActivCtlModel::getStatus, SysActivCtlModel.STATUS_UNUSED);
        return getModelMapper().first(humanCondition);
    }

     /**
      * 选择有效
      *
      * @return {@link SysActivCtlModel }
      */
     @Override
    public SysActivCtlModel selectValid() {
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(SysActivCtlModel::getStatus, SysActivCtlModel.STATUS_VALID);
        return getModelMapper().first(humanCondition);
    }

     /**
      * 除一项外其他无效
      *
      * @param exceptedId
      */
     @Override
    public void invalidOtherExceptOne(String exceptedId) {
        HumanCondition humanCondition = new HumanCondition()
                .filterNotEqual(SysActivCtlModel::getId, exceptedId);
        SysActivCtlModel model = new SysActivCtlModel();
        model.setStatus(SysActivCtlModel.STATUS_INVALID);
        getModelMapper().filterUpdate(model, humanCondition);
    }

     /**
      * @return {@link List }<{@link SysActivCtlModel }>
      */
     private List<SysActivCtlModel> selectAll() {
        List<SysActivCtlModel> list = getModelMapper().list(new HumanCondition());
        return list;
    }
}
