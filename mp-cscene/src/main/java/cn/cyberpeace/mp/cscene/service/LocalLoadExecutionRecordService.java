package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.cscene.model.LocalLoadExecutionRecordModel;
import cn.cyberpeace.mp.cscene.model.ScreenshotModel;
import cn.cyberpeace.mp.cscene.request.queryparam.ScreenshotQueryParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* @author liyahao
* @description 针对表【mp_local_load_execution_record(本地载荷执行记录表)】的数据库操作Service
* @createDate 2024-12-27 17:28:05
*/
public interface LocalLoadExecutionRecordService extends BaseModelService<LocalLoadExecutionRecordModel, ScreenshotQueryParams> {

}
