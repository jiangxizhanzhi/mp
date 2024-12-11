package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 软件/固件版本库
 * @TableName mp_version_library
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(value ="mp_version_library")
public class VersionLibraryModel extends BaseModel {

    /**
     * 名称
     */
    @ApiModelProperty(value = "name")
    private String name;

    /**
     * 固件版本
     */
    @ApiModelProperty(value = "firmware_version")
    private String firmwareVersion;

    /**
     * 适用对象
     */
    @ApiModelProperty(value = "applicable_object")
    private String applicableObject;

    /**
     * 软件文件名
     */
    @ApiModelProperty(value = "software_name")
    private String softwareName = "";

    /**
     * 固件地址
     */
    @ApiModelProperty(value = "firmware_address")
    private String firmwareAddress = "";

    /**
     * 类型(0:软件 1:固件)
     */
    @ApiModelProperty(value = "type")
    private Integer type;

}