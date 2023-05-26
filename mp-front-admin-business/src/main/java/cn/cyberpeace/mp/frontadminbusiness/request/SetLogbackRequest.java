package cn.cyberpeace.mp.log;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @BelongsPackage: cn.cyberpeace.mp.log
 * @Author: hhuang
 * @CreateTime: 2023/05/26  14:59
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class SetLogbackRequest
{
    @NotNull
    private Integer maxHistory;
}
