package cn.cyberpeace.mp.devops.cluster.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnapshotRequest {

    /**
     * 镜像ID
     */
    private String uuid;

    /**
     * 镜像类型
     */
    private String imageType;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;
}
