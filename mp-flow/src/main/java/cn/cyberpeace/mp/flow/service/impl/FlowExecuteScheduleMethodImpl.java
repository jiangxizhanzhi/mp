package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.flow.model.base.FlowBaseModel;
import cn.cyberpeace.mp.flow.service.ScheduleMethod;
import org.springframework.stereotype.Service;

/**
 * @Description: 流量执行实现类
 * @Autho qiaokang
 * @Date：2021/6/16 15:51
 */
@Service
public class FlowExecuteScheduleMethodImpl implements ScheduleMethod {

    @Override
    public void execute(FlowBaseModel model) {
        // TODO 调用流量执行接口
        System.out.println("hello how are you " + model.toString());
    }
}
