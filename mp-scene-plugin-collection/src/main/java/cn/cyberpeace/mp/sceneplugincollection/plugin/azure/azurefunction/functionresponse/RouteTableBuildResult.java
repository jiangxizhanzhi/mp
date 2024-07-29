package cn.cyberpeace.mp.sceneplugincollection.plugin.azure.azurefunction.functionresponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 防火墙创建结果
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteTableBuildResult {

    //创建结果
    private Boolean result;

    //错误信息
    private String errorMessage;
}
