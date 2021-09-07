package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.flow.model.base.FlowBaseModel;

/**
 * @Description: 定时任务执行方法接口
 * @Author qiaokang
 * @Date：2021/6/16 14:50
 */
public interface ScheduleMethod {

    void execute(FlowBaseModel obj);

}
