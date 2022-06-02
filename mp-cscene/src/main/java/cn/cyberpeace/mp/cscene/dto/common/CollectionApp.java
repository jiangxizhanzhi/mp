package cn.cyberpeace.mp.cscene.dto.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/18 17:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class CollectionApp {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "编码")
    private String code;

}
