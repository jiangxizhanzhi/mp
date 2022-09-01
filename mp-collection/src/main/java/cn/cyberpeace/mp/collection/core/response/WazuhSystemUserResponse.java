package cn.cyberpeace.mp.collection.core.response;

import cn.cyberpeace.mp.collection.core.model.WazuhSystemUser;
import lombok.Data;

import java.util.List;

@Data
public class WazuhSystemUserResponse {
    private List<WazuhSystemUser> values;
    private List<WazuhSystemUser> modified;
    private List<WazuhSystemUser> deletedPrev;
    private List<WazuhSystemUser> added;
}
