package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_devops_screenshot")
public class ScreenshotModel extends BaseModel {

	
	@ApiModelProperty(value = "截屏文件地址")
	@DefaultStr
	private String file;

	
	@ApiModelProperty(value = "截屏文件名称")
	@DefaultStr
	private String fileName;

	
	@ApiModelProperty(value = "截屏文件大小，单位:字节(B)")
	private Long fileSize;

	
	@ApiModelProperty(value = "场景名称")
	@DefaultStr
	private String sceneName;

	
	@ApiModelProperty(value = "用户id")
	@DefaultStr
	private String userId;

	
	@ApiModelProperty(value = "用户名称")
	@DefaultStr
	private String userName;

	
	@ApiModelProperty(value = "组织名称")
	@DefaultStr
	private String userOrgName;

	
	@ApiModelProperty(value = "场景id")
	@DefaultStr
	private String scene;

	
	@ApiModelProperty(value = "节点id")
	@DefaultStr
	private String nodeId;


}
