package cn.cyberpeace.mp.auth.dto;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.service.OrgOperation;
import lombok.Data;

import java.util.List;

@Data
public class OrgDto extends OrgModel {

    OrgDto parentOrg;

    List<OrgDto> children;

    public <T> T execute(OrgOperation<T> operation) {
        return operation.doExecute(this);
    }
}
