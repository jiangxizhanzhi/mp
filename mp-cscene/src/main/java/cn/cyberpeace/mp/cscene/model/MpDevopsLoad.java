package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.cscene.constant.LoadConvertCode;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 载荷同步表(MpDevopsLoad)实体类
 *
 * @since 2024-03-28 16:41:10
 */
@Data
@Table("mp_devops_load")
public class MpDevopsLoad extends BaseModel {

    /**
     * 主键
     */
    private Integer pk;
    /**
     * ID
     */
    private String id;
    /**
     * 工具名称
     */
    private String toolName;
    /**
     * 镜像名称
     */
    private String imageName;
    /**
     * 工具描述
     */
    private String toolDescription;
    /**
     * 作用类型
     */
    private String actionType;
    /**
     * 运行类型名称
     */
    private String actionTypeName;
    /**
     * 运行平台
     */
    private String runTable;
    /**
     * 运行平台名称
     */
    private String runTableName;
    /**
     * 控制类型
     */
    private String controlType;
    /**
     * 控制类型名称
     */
    private String controlTypeName;
    /**
     * 控制端口
     */
    private String controlPort;
    /**
     * 来源
     */
    private String source;
    /**
     * 附件
     */
    private String attachment;
    private String attachmentName;
    /**
     * 帮助文档
     */
    private String readMe;
    private String readMeName;
    /**
     * 封面
     */
    private String cover;
    private String coverName;
    /**
     * 默认命令
     */
    private String defaultCmd;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 同步时间
     */
    private LocalDateTime synchronizationTime;
    /**
     * 创建人Id
     */
    private String creatorId;
    /**
     * 漏洞情报
     */
    private List Loopholes;
    /**
     * 镜像类型
     */
    private String imageType;
    /**
     * role
     */
    private Integer role;
    /**
     * 漏洞编号
     */
    private String vulneCode;
    /**
     * 脚本文件地址
     */
    private String scriptFilePath;
    /**
     * 脚本文件名称
     */
    private String scriptFile;
    /**
     * 脚本类型
     */
    private Integer scriptType;
    /**
     * 脚本代码
     */
    private String scriptCode;
    /**
     * 技战法
     */
    private Integer technology;

    /**
     * 是否投送
     */
    private Boolean delivery;

    public void setConvertRunTable(String runTable) {
        String s = LoadConvertCode.getRunTableConvertMap().get(runTable);
        this.runTable = s != null ? s : LoadConvertCode.RUN_TABLE_OTHER;
    }

    public void setConvertControlType(String controlType) {
        String s = LoadConvertCode.getControlTypeConvertMap().get(controlType);
        this.controlType = s != null ? s : LoadConvertCode.CONTROL_TYPE_OTHER;
    }

    public void setConvertActionType(String actionType) {
        String s = LoadConvertCode.getActionTypeConvertMap().get(actionType);
        this.actionType = s != null ? s : LoadConvertCode.ACTION_TYPE_OTHER;
    }

    public void setActionTypeConvertName(String actionType, Map<String, String> convertMap) {
        if (StringUtils.hasText(actionType)) {
            // 如果 actionType 包含逗号，处理为列表
            if (actionType.contains(",")) {
                List<String> actionTypeName = Arrays.stream(actionType.split(","))
                        .map(convertMap::get)
                        .filter(StringUtils::hasText) // 过滤空值
                        .collect(Collectors.toList());
                // 将列表转换为逗号分隔的字符串
                this.actionTypeName = String.join(",", actionTypeName);
            } else {
                // 单个 actionType 处理
                this.actionTypeName = convertMap.get(actionType);
            }
        }
    }
}