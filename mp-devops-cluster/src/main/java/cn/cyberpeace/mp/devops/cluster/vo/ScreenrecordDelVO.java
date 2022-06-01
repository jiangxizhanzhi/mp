package cn.cyberpeace.mp.devops.cluster.vo;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
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
public class ScreenrecordDelVO extends BaseCloudPlatformResult {

    private boolean success;

    private String messages;

    private String requestId;

}
