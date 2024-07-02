package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.org.OrgDto;

/**
 * @ClassName OrgDtoOperation
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 */
public interface OrgDtoOperation<T> {

    T doExecute(OrgDto orgDto);
}
