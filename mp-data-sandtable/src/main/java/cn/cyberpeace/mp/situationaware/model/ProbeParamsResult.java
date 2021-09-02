package cn.cyberpeace.mp.situationaware.model;

import io.swagger.annotations.ApiModel;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("探针参数")
public class ProbeParamsResult {

    private String id;

    private String probeId;

    private Integer paramType;

    private String defaultVal;

    private String paramDes;

    private Date createTime;

    private Date updateTime;

    private Integer delFlag;

    private String paramName;
}
