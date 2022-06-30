package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.log.dto.LogOperateDTO;
import cn.cyberpeace.mp.devops.log.enums.SiteTypeEnums;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import cn.cyberpeace.mp.devops.log.service.OperateLogServiceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/29
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DeviceLogAsyncService {

    private final OperateLogServiceFactory operateLogServiceFactory;

    /**
     *
     * @param dto
     * @param request
     * @param <T>
     */
    public  <T> void log(LogOperateDTO dto,HttpServletRequest request){
        try {
            dto.setSiteTypeEnums(SiteTypeEnums.HLD);
            OperationLogModel hldLog = operateLogServiceFactory.buildOperationLogModel(dto,request);
            operateLogServiceFactory.create(hldLog);
        }catch (Exception e){
            log.info("async operate log to hld error ===>",e);
        }

    }
}
