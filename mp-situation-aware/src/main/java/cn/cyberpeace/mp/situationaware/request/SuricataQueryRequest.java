package cn.cyberpeace.mp.situationaware.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuricataQueryRequest {

    @ApiModelProperty("协议")
    private String protocol;

    @ApiModelProperty("规则名称")
    private String ruleName;

    @ApiModelProperty("Att&ck技术")
    private String attck;

    @ApiModelProperty(value = "当前页")
    private Integer page;

    @ApiModelProperty(value = "每页数量")
    private Integer pageSize;

    @ApiModelProperty(value = "每页数量(兼容前端)")
    private Integer perPage;

    public void ensure() {
        
        if (Objects.isNull(page)) {
            page = 1;
        }
        if (Objects.isNull(pageSize)) {
            if (Objects.nonNull(perPage)) {
                pageSize = perPage;
            } else {
                pageSize = 10;
            }
        }
    }

}
