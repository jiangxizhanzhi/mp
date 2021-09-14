package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 录屏删除返回
 * @Author qiaokang
 * @Date：2021/9/13 19:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreenrecordDelResponse extends BaseCloudPlatformModel {

    private boolean success;

    private String messages;

    private String requestId;

}
