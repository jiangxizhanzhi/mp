package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @Description 告警统计信息对象
 * @Author qiaokang
 * @Date 2022-07-19 16:17:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("mp_devops_alarm_statistics")
public class AlarmStatisticsModel extends BaseModel {

	/**
	 * 告警类型：0 云平台服务, 1 业务服务 2 服务器
	 */
	@ApiModelProperty(value = "告警类型：0 云平台服务, 1 业务服务 2 服务器")
	private Integer alarmType;

	/**
	 * 告警数量
	 */
	@ApiModelProperty(value = "告警数量")
	private int alarmCount;

	/**
	 * 告警日期
	 */
	@ApiModelProperty(value = "告警日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate alarmDate;


}
