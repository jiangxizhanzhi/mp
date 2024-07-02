package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.target.TargetBo;

/**
 * @ClassName TargetOperation
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 */
public interface TargetOperation<T> {

    /**
     * doExecute
     *
     * @param targetBo
     * @return
     */
    T doExecute(TargetBo targetBo);
}
