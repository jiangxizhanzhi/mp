package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddStandardsTaskRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	
	private String name;

	
	private String description;

	
	private String port;

	
	private String targetIp;

	
	private String sourceIp;

	
	private int type = 4;

	
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
