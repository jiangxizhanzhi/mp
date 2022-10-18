package cn.cyberpeace.mp.collection.bo.event;

import cn.cyberpeace.mp.collection.bo.base.BaseEventBO;
import lombok.Data;

@Data
public class ModifyEventBO extends BaseEventBO {
    /**
     * 文件或注册表路径
     */
    private String path;

    /**
     * 文件大小
     */
    private String size;

    /**
     * 用户
     */
    private String user;

    /**
     * 动作
     */
    private String action;

    /**
     * 变更前md5
     */
    private String md5Before;

    /**
     * 变更后md5
     */
    private String md5After;
}
