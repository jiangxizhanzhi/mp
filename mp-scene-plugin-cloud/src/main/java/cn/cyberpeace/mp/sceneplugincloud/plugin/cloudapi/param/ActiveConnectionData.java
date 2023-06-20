package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: guquan
 * @Date：2023/6/20 9:52
 */
@Data
public class ActiveConnectionData {

    private List<String> currentConnections;

    private Boolean success;
}
