package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.TargetDto;

public interface TargetOperation<T> {

    T doExecute(TargetDto targetDto);
}
