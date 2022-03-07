package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 截屏文件管理对象
 * @Author qiaokang
 * @Date 2021-09-13 11:05:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_devops_screenshot")
public class ScreenshotModel extends BaseModel {

	/**
	 * 截屏文件地址
	 */
	@ApiModelProperty(value = "截屏文件地址")
	@DefaultStr
	private String file;

	/**
	 * 截屏文件名称
	 */
	@ApiModelProperty(value = "截屏文件名称")
	@DefaultStr
	private String fileName;

	/**
	 * 截屏文件大小，单位:字节(B)
	 */
	@ApiModelProperty(value = "截屏文件大小，单位:字节(B)")
	private Long fileSize;

	/**
	 * 场景名称
	 */
	@ApiModelProperty(value = "场景名称")
	@DefaultStr
	private String sceneName;

	/**
	 * 用户id
	 */
	@ApiModelProperty(value = "用户id")
	@DefaultStr
	private String userId;

	/**
	 * 用户名称
	 */
	@ApiModelProperty(value = "用户名称")
	@DefaultStr
	private String userName;

	/**
	 * 组织名称
	 */
	@ApiModelProperty(value = "组织名称")
	@DefaultStr
	private String userOrgName;

	/**
	 * 场景id
	 */
	@ApiModelProperty(value = "场景id")
	@DefaultStr
	private String scene;

	/**
	 * 节点id
	 */
	@ApiModelProperty(value = "节点id")
	@DefaultStr
	private String nodeId;


}
