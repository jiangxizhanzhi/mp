 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserLogService.java.
 // File size 1156.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.log.controller.OperateLogController;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.log.response.OperationLogPageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

 /**
  * 用户日志服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Service
@Slf4j
public class UserLogService {

     /**
      * 操作日志控制器
      */
     @Autowired
    private OperateLogController operateLogController;

     /**
      * 列表操作日志
      *
      * @param request 请求
      * @return {@link List }<{@link OperationLogPageResponse }>
      */
     public List<OperationLogPageResponse> listOpLog(OperationLogPageRequest request) {
        ListObjServiceResult<OperationLogPageResponse> response = operateLogController.page(request);
        if (Objects.nonNull(response.getData().getList())) {
            List<OperationLogPageResponse> list = response.getSource().getList();
            return list;
        }
        return new ArrayList<>();
    }


}
