package cn.cyberpeace.mp.datasandtable.model;

import io.swagger.annotations.ApiModel;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("探针参数")
public class ProbeResult {
    private Integer pk;

    private String id;

    private String probeName;

    private String des;

    private String osName;

    private Integer type;

    private Integer scriptType;

    private Date createTime;

    private Date updateTime;

    private Integer delFlag;

    private byte[] scriptContent;

    private List<ProbeParamsResult> paramsResultList;
}
