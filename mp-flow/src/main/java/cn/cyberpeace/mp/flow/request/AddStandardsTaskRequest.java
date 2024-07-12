 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AddStandardsTaskRequest.java.
 // File size 1659.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @ClassName AddStandardsTaskRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddStandardsTaskRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	* 任务名称
	*/
	private String name;

	/**
	* 任务的描述信息
	*/
	private String description;

	/**
	* 网口的pci号
	*/
	private String port;

	/**
	* 目标ip
	*/
	private String targetIp;

	/**
	* 源ip
	*/
	private String sourceIp;

	/**
	* 任务类型:1 DDOS  2 扫描 3 爆破  4 其他，默认用4
	*/
	private int type = 4;

	/**
	* 攻击脚本类型的资源文件的uuid
	*/
	private String fileResourceUuid;


	public AddStandardsTaskRequest(FlowTaskModel flowTaskModel) {
		this.setName(flowTaskModel.getTitle() + System.currentTimeMillis());
		this.setDescription(flowTaskModel.getTaskDesc());
		this.setTargetIp(flowTaskModel.getTargetIps().split(BaseConstants.SHORT_DASH)[0]);
		if (StringUtils.isBlank(flowTaskModel.getOriginIps())) {
			flowTaskModel.setOriginIps("127.0.0.1-127.0.0.1");
		}
		this.setSourceIp(flowTaskModel.getOriginIps().split(BaseConstants.SHORT_DASH)[0]);
		this.setFileResourceUuid(flowTaskModel.getThirdFlowId());
	}
}
