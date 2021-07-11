package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("mp_devops_system_alarm")
public class SystemAlarmModel extends BaseModel {

	public static final String QUERY_FIELD_ALARM_TYPE = "alarm_type";
	public static final String QUERY_FIELD_CNT = "cnt";



	
	@ApiModelProperty(value = "告警类型：0 云平台服务, 1 业务服务 2 服务器")
	private Integer alarmType;

	
	@ApiModelProperty(value = "告警信息")
	@DefaultStr
	private String alarmMsg;

	
	@ApiModelProperty(value = "节点名称")
	@DefaultStr
	private String hostname;

	
	@ApiModelProperty(value = "设备名称")
	@DefaultStr
	private String servername;

	
	@ApiModelProperty(value = "持续时间")
	private Integer runningTime;

	
	@ApiModelProperty(value = "告警状态：0 生效， 1失效")
	private Integer alarmStatus;


}
