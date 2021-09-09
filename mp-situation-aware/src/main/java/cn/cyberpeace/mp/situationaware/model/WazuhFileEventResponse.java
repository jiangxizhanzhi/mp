package cn.cyberpeace.mp.situationaware.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 事件查询返回结果
 * @Author qiaokang
 * @Date：2021/6/22 17:20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WazuhFileEventResponse {

    @ApiModelProperty("事件集合")
    private List<FileEventModel> fileEventModelList;

    @ApiModelProperty("事件总数")
    private int total;

}
