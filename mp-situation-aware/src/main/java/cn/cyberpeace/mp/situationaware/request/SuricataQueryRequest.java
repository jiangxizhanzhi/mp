package cn.cyberpeace.mp.situationaware.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/*
 * @BelongsPackage: cn.cyberpeace.mp.situationaware.request
 * @Author: hhuang
 * @CreateTime: 2023/04/23  14:41
 * @Description: Suricata规则查询参数
 * @Version: 1.0
 */
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

    public void ensure() {
        // 默认值
        if (Objects.isNull(page)) {
            page = 1;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = 10;
        }
    }

}
