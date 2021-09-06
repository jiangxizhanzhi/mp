package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.target.TargetDto;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public interface TargetOperation<T> {

    T doExecute(TargetDto targetDto);
}
