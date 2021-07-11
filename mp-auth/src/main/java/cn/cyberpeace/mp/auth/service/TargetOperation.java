package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.target.TargetBo;


public interface TargetOperation<T> {

    T doExecute(TargetBo targetBo);
}
