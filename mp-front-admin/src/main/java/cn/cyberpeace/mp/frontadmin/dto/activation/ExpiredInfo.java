package cn.cyberpeace.mp.frontadmin.dto.activation;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsPackage: cn.cyberpeace.mp.frontadmin.dto.activation
 * @Author: hhuang
 * @CreateTime: 2023/05/23  15:13
 * @Description: license过期信息
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpiredInfo {

    @ApiModelProperty("过期日期")
    private String expiredDate;

    @ApiModelProperty("过期前提醒天数")
    private Integer remindDays;
}
