package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.OrgDto;

public interface OrgOperation<T> {

    T doExecute(OrgDto orgDto);
}
