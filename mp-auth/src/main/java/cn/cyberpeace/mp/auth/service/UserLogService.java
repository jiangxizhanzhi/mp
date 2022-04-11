package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devops.log.controller.OperateLogController;
import cn.cyberpeace.mp.devops.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.devops.log.response.OperationLogPageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
public class UserLogService {

    @Autowired
    private OperateLogController operateLogController;

    public List<OperationLogPageResponse> listOpLog(OperationLogPageRequest request) {
        ListObjServiceResult<OperationLogPageResponse> response = operateLogController.page(request);
        if (Objects.nonNull(response.getData().getList())) {
            List<OperationLogPageResponse> list = response.getSource().getList();
            return list;
        }
        return new ArrayList<>();
    }


}
