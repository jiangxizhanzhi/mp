package cn.cyberpeace.mp.devops.cluster.response;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 磁盘剩余空间返回对象
 * @Author qiaokang
 * @Date：2022/3/10 10:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "磁盘剩余空间返回对象")
public class ImportDiskSpaceResponse extends BaseCloudPlatformResponse {

    /**
     * 镜像空间
     */
    private Long dockerSpace;

    /**
     * 虚拟机空间
     */
    private Long vmSpace;

}
