package cn.cyberpeace.mp.collection.bo.host;

import cn.cyberpeace.mp.collection.bo.base.BaseEventBO;
import lombok.Data;

@Data
public class ProcessBO extends BaseEventBO {
    private String status;

    private String parentName;

    private String createTime;

    private String parentId;

    private String vms;

    private String cwd;

    private String rss;

    private String priority;

    private String numThreads;

    private String command;

    private String username;

    private String pid;

    private String exe;

    private String name;
}
