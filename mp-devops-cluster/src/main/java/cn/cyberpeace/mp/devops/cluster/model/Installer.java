package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.auth.dto.common.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Installer extends BaseCloudPlatformModel{

    /**
     * ID
     */
    private Integer id;

    /**
     * UUID
     */
    private String uuid;

    /**
     * 创建时间
     */
    private String createdAt;

    /**
     * 更新时间
     */
    private String updatedAt;

    /**
     * 是否删除
     */
    private Boolean deleted;

    /**
     * 删除时间
     */
    private String deletedAt;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 图标
     */
    private String logo;

    /**
     * 分类
     */
    private String categories;

    /**
     * 服务类型：windows服务/docker服务
     */
    private Integer deployType;

    /**
     * docker
     */
    private ExtDocker extDocker;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ExtDocker{

        /**
         * ID
         */
        private Integer id;

        /**
         * UUID
         */
        private String installerUuid;

        /**
         * 文件地址
         */
        private String file;

        private String settings;

        private Integer syncStatus;

    }
}
