package cn.cyberpeace.mp.collection.response;

import cn.cyberpeace.mp.collection.bo.host.SystemUserBO;
import lombok.Data;

import java.util.List;

@Data
public class SystemUserBOResponse {
    private List<SystemUserBO> values;

    private List<SystemUserBO> modified;

    private List<SystemUserBO> deletedPrev;

    private List<SystemUserBO> added;
}
