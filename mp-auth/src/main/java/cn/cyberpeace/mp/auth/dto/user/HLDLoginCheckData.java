package cn.cyberpeace.mp.auth.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description:
 * @Author hewei
 * @Date：2021/2/18 18:11
 */
@Data
@ApiModel
public class HLDLoginCheckData {

    private String userid;

    private String username;

    private List<String> roleName;

    private String plugininfo;

    private String version;

    private long expireMinute;

    private long expireFreshTime;
}
