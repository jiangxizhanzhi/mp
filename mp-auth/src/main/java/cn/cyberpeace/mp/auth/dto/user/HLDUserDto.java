package cn.cyberpeace.mp.auth.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDate;

/**
 * @Description:
 * @Author hewei
 * @Date：2021/2/17 11:13
 */
@Data
@ApiModel
public class HLDUserDto {

    @ApiModelProperty(value = "HLD人员id")
    private BigInteger id;

    @ApiModelProperty(value = "密码")
    private String passWord;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "账号")
    private String account;

    @ApiModelProperty(value = "用户名称")
    private String name;

    @ApiModelProperty(value = "出生年月")
    private LocalDate birthday;

    @ApiModelProperty(value = "移动电话")
    private String mobilePhone;

    @ApiModelProperty(value = "邮箱地址")
    private String e_mail;

    @ApiModelProperty(value = "通讯地址")
    private String postalAddress;

    @ApiModelProperty(value = "描述")
    private String mhDescribe;

    @ApiModelProperty(value = "更新时间")
    private String userUpdateTime;
}
