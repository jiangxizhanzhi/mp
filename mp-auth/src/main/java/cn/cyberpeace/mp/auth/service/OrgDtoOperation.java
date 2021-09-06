package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.OrgDto;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public interface OrgDtoOperation<T> {

    T doExecute(OrgDto orgDto);
}
