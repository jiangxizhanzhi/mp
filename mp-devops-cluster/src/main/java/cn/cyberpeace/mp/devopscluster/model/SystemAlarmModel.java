package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 告警信息对象
 * @Author qiaokang
 * @Date 2022-07-18 17:35:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("mp_devops_system_alarm")
public class SystemAlarmModel extends BaseModel {

	public static final String QUERY_FIELD_ALARM_TYPE = "alarm_type";
	public static final String QUERY_FIELD_CNT = "cnt";



	/**
	 * 告警类型：0 云平台服务, 1 业务服务 2 服务器
	 */
	@ApiModelProperty(value = "告警类型：0 云平台服务, 1 业务服务 2 服务器")
	private Integer alarmType;

	/**
	 * 告警信息
	 */
	@ApiModelProperty(value = "告警信息")
	@DefaultStr
	private String alarmMsg;

	/**
	 * 节点名称
	 */
	@ApiModelProperty(value = "节点名称")
	@DefaultStr
	private String hostname;

	/**
	 * 设备名称
	 */
	@ApiModelProperty(value = "设备名称")
	@DefaultStr
	private String servername;

	/**
	 * 持续时间
	 */
	@ApiModelProperty(value = "持续时间")
	private Integer runningTime;

	/**
	 * 告警状态：0 生效， 1失效
	 */
	@ApiModelProperty(value = "告警状态：0 生效， 1失效")
	private Integer alarmStatus;


}
