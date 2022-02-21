package cn.cyberpeace.mp.auth.dto.user;


import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class HLDUserResponse {
    private String msg;

    private Integer code;

    private List<HLDUserDto> info;
}
