package cn.cyberpeace.mp.behavior.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table("mp_network_behavior")
public class NetworkBehaviorModel extends BaseModel {

	@ApiModelProperty("网络行为名称")
	private String name;

	@ApiModelProperty("网络行为描述")
	private String description;

	@ApiModelProperty("网络行为类型")
	private Integer type;

	@ApiModelProperty("文件名称")
	private String fileName;

	@ApiModelProperty("文件路径")
	private String fileUrl;

	public enum BehaviorEnum {

		NETWORK(0, "浏览网页"),
		EMAIL(1, "发送邮件"),
		FILE(2, "下载文件"),
		FORM(3, "填写和提交表单"),
		OTHER(4, "其他");

		@Getter
		private Integer code;

		@Getter
		private String desc;

		BehaviorEnum(Integer code, String desc) {
			this.code = code;
			this.desc = desc;
		}
	}
}
