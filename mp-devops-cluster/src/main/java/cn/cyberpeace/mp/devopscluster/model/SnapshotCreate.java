package cn.cyberpeace.mp.devopscluster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName SnapshotCreate
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnapshotCreate {

    /**
     * UUID
     */
    private String deviceUuid;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;
}
