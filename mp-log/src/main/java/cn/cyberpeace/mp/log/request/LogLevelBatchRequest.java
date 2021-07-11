package cn.cyberpeace.mp.log.request;

import cn.cyberpeace.mp.log.bo.LogLevelBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogLevelBatchRequest implements Serializable {

    private static final long serialVersionUID = -5162943967528676332L;

    @ApiModelProperty(value = "日志")
    private List<LogLevelBO> logLevelList;


}
