package cn.cyberpeace.mp.flow.util;

import cn.cyberpeace.mp.flow.request.FlowExecuteRequest;
import cn.cyberpeace.mp.flow.service.impl.FlowExecuteScheduleMethodImpl;
import lombok.Builder;

/**
 * @Description: 流量执行定时任务
 * @Autho qiaokang
 * @Date：2021/6/16 16:30
 */
@Builder
public class FlowExecuteScheduleUtil extends BaseScheduleUtil<FlowExecuteRequest, FlowExecuteScheduleMethodImpl> {

}
