package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.org.OrgDto;


public interface OrgDtoOperation<T> {

    T doExecute(OrgDto orgDto);
}
