package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.OrgDto;

public interface OrgDtoOperation<T> {

    T doExecute(OrgDto orgDto);
}
