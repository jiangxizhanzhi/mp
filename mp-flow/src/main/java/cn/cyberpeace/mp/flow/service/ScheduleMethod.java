package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.flow.model.base.FlowBaseModel;

/**
 * @ClassName ScheduleMethod
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface ScheduleMethod {

    /**
     * execute
     *
     * @param obj
     */
    void execute(FlowBaseModel obj);

}
