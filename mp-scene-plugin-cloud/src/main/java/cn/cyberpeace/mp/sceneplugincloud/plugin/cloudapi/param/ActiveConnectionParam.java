package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: guquan
 * @Date：2023/6/20 9:49
 */
@Data
public class ActiveConnectionParam implements CloudHttpData {

    private List<String> connectionIds;

}
