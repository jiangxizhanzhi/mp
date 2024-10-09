package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 载荷执行结果文件导入
 */
@Data
@NoArgsConstructor
@Table("mp_devops_load_scene_upload_log_file")
public class MpDevopsLoadSceneUploadLogFile {

    /**
     * 主键
     */
    private Integer pk;

    /**
     * 场景id
     */
    private String sceneId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String file;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人Id
     */
    private String creatorId;
}