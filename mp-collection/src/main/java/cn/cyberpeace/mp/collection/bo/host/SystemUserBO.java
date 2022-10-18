package cn.cyberpeace.mp.collection.bo.host;

import cn.cyberpeace.mp.collection.bo.base.BaseEventBO;
import lombok.Data;

@Data
public class SystemUserBO extends BaseEventBO {
    private String comment;

    private String shell;

    private String name;

    private String groupName;

    private String groupId;

    private String id;

    private String dir;
}
