package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("mp_devops_alarm_statistics")
public class AlarmStatisticsModel extends BaseModel {

	
	@ApiModelProperty(value = "告警类型：0 云平台服务, 1 业务服务 2 服务器")
	private Integer alarmType;

	
	@ApiModelProperty(value = "告警数量")
	private int alarmCount;

	
	@ApiModelProperty(value = "告警日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate alarmDate;


}
