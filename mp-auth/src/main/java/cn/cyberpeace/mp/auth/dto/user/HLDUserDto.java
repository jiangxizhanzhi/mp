package cn.cyberpeace.mp.auth.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @ApiModelProperty(value = "用户code")
    private String userCode;

    @ApiModelProperty(value = "人员名称")
    private String userName;

    @ApiModelProperty(value = "密码")
    private String passWord;

    @ApiModelProperty(value = "确认密码")
    private String oncePassword;

    @ApiModelProperty(value = "性别")
    private String userSex;

    @ApiModelProperty(value = "用户身份 0:白方人员 1:作战人员")
    private String identity;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "账号")
    private String account;

    @ApiModelProperty(value = "所属部队")
    private String troops;

    @ApiModelProperty(value = "军衔")
    private String militaryRank;

    @ApiModelProperty(value = "身高")
    private String height;

    @ApiModelProperty(value = "民族")
    private String nation;

    @ApiModelProperty(value = "用户名称")
    private String name;

    @ApiModelProperty(value = "政治面貌")
    private String poloticalOutliik;

    @ApiModelProperty(value = "第一学历")
    private String firstDegree;

    @ApiModelProperty(value = "健康状况")
    private String health;

    @ApiModelProperty(value = "文化程度")
    private String educationDegree;

    @ApiModelProperty(value = "出生年月")
    private LocalDate birthday;

    @ApiModelProperty(value = "工作时间")
    private LocalDate jobTime;

    @ApiModelProperty(value = "入伍时间")
    private LocalDate recruitmentTime;

    @ApiModelProperty(value = "办公电话")
    private String officeTelephone;

    @ApiModelProperty(value = "移动电话")
    private String mobilePhone;

    @ApiModelProperty(value = "邮箱地址")
    private String e_mail;

    @ApiModelProperty(value = "通讯地址")
    private String postalAddress;

    @ApiModelProperty(value = "描述")
    private String mhDescribe;

    @ApiModelProperty(value = "录入时间")
    private LocalDate userFoundTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime userUpdateTime;

    @ApiModelProperty(value = "")
    private String identitys;

    @ApiModelProperty(value = "")
    private String roleId;

    @ApiModelProperty(value = "")
    private String systemList;

    @ApiModelProperty(value = "")
    private String adminId;
}
