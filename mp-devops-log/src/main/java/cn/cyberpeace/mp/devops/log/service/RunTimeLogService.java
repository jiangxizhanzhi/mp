package cn.cyberpeace.mp.devops.log.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.devops.log.response.RunTimeLogResponse;
import org.springframework.stereotype.Service;

/**
  *@Desciption 
  *@Author zhangjin
  *@Date 9/13/21 9:46 上午
**/
@Service
public class RunTimeLogService {

    public ListObjResult<RunTimeLogResponse> queryRuntimeLog() {
        return new ListObjResult<>();
    }
}
