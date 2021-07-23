package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.model.OrgModel;

public interface OrgModelOperation<T> {

    T doExecute(OrgModel orgModel);
}
