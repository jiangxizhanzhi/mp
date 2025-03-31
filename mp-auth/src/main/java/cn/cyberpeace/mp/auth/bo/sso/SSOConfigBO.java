package cn.cyberpeace.mp.auth.bo.sso;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.bo.sso
 * @Author: hhuang
 * @CreateTime: 2025/03/31  16:30
 * @Description: 可暴露的配置信息
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SSOConfigBO {

    @ApiModelProperty("默认重定向地址")
    private String defaultRedirectUrl;

}
