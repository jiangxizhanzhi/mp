 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AlarmStatisticsModel.java.
 // File size 1182.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
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

/**
 * @ClassName AlarmStatisticsModel
 * @Description 告警统计信息对象
 * @Author qiaokang
 * @Date 2022-07-19 16:17:26
 * @Version 1.0.0
 * @see BaseModel
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
