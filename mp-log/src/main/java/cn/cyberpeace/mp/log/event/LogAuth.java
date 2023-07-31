package cn.cyberpeace.mp.log.event;

import lombok.Data;

/**
 * @BelongsPackage: cn.cyberpeace.mp.log.event
 * @Author: hhuang
 * @CreateTime: 2023/03/31  14:59
 * @Description: log调用auth数据类
 * @Version: 1.0
 */
@Data
public class LogAuth {

    /**
     * 需要的用户id
     */
    private String[] userIds;
}
