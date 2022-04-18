package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.login.LoginResp;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.dto.user.UserRequest;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserHeartbeatService;
import cn.cyberpeace.mp.auth.service.UserOnlineRecordService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.param.UserQueryParams;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.log.annotation.OperateLog;
import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTypeEnums;
import com.unionpay.common.userclient.UserInfoUtil;
import com.unionpay.common.userclient.vo.BaseUserInfo;
import com.unionpay.common.userclient.vo.User;
import edu.yale.its.tp.cas.client.filter.CASFilter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

/**
 * @Description: 用户管理接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "用户管理接口", tags = "AUTH-用户管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/users")
public class UserController extends BaseModelController<UserModel, UserQueryParams, UserService> {

    @Autowired
    private UserOnlineRecordService userOnlineRecordService;

    @Autowired
    private UserHeartbeatService heartbeatService;

    @Autowired
    public UserController(UserService modelService) {
        setModelService(modelService);
    }

    private static final String sysIdstr = "F7EB8C529C1A503B79E729581EDBC95E";

    public static final String defaultOrg = "a13262d600fe40c799d63dce5c1485af";

    public static final String defaultRole = "5827ff7847504ffd806e0a85c417228d";


    @Override
    @ApiIgnore
    @ApiOperation(value = "(弃用，建议用/api/mp/auth/org_users/page/byorg) 分页查询组织下的用户", notes = "")
    @GetMapping
    public ListObjServiceResult<UserModel> list(UserQueryParams queryParams) {
        return super.list(queryParams);
    }

    @Override
    @ApiIgnore
    @ApiOperation(value = "(弃用，建议用/add) 创建数据", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @PostMapping
    public ObjServiceResult<UserModel> create(@RequestBody UserModel obj, @RequestParam(required = false) String[] fields) {
        return super.create(obj, fields);
    }

    @Override
    @ApiOperation(value = "(弃用，建议用/modify)更新指定id数据", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @PatchMapping("/{id}")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "修改用户", target = OpTargetEnums.USER)
    public ObjServiceResult<UserModel> update(@PathVariable String id, @RequestBody UserModel obj,
                                              @RequestParam(required = false) String[] fields) {
        return super.update(id, obj, fields);
    }

    @Override
    @ApiOperation("删除指定id用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
    })
    @DeleteMapping("/{id}")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.DELETE, title = "删除用户", target = OpTargetEnums.USER)
    public ServiceResult<Boolean> delete(@PathVariable String id) {
        return super.delete(id);
    }


    @Override
    @ApiOperation(value = "查询指定id数据 附带组织，角色信息", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @GetMapping("/{id}")
    public ObjServiceResult get(@PathVariable String id, @RequestParam(required = false) String[] fields) {
        UserDto userDto = getModelService().getUserDto(id);
        return ResultUtils.obj2ObjServiceResult(userDto);
    }


    @ApiOperation(value = "查询当前登录用户信息 附带组织，角色信息", notes = "")
    @GetMapping("/current")
    public ServiceResult<UserDto> getCurrentUser() {
        UserDto userDto = getModelService().getCurrentUserDto();
        return ServiceResult.success(userDto);
    }


    @ApiOperation(value = "创建用户，同时完成组织，角色绑定关系", notes = "")
    @PostMapping("/add")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.CREATE, title = "新增用户", target = OpTargetEnums.USER)
    public ServiceResult<UserModel> add(@RequestBody UserRequest createRequest) {
        UserModel userModel = getModelService().add(createRequest);
        return ServiceResult.success(userModel);
    }

    @ApiOperation(value = "修改用户，同时完成组织，角色绑定关系", notes = "")
    @PostMapping("/modify")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "更新用户", target = OpTargetEnums.USER)
    public ServiceResult<UserModel> modify(@RequestBody UserRequest modifyRequest) {
        UserModel userModel = getModelService().modify(modifyRequest);
        return ServiceResult.success(userModel);
    }

    @ApiOperation(value = "银联用户定制化登录", notes = "")
    @GetMapping("/yinlian/login")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.LOGIN, title = "用户登录", target = OpTargetEnums.USER)
    public ServiceResult<LoginResp> newLogin(@RequestBody HttpServletRequest request) {
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute(CASFilter.CAS_FILTER_USER);
        String institutedId = (String) session.getAttribute(CASFilter.CAS_FILTER_INSTITUTE);
        BaseUserInfo userInfo = new BaseUserInfo();
        boolean canLogin = false;
        try {
            userInfo = UserInfoUtil.getLoginUserAndPrivileges(userId, institutedId, sysIdstr);
            canLogin = UserInfoUtil.authenticate(userInfo, null);
        } catch (Exception e) {
            log.error("get user failed,msg:{}", e);
        }
        if (!canLogin) {
            return ServiceResult.fail("this user can not login");
        }
        if (userInfo != null) {
            if (userInfo.getUser() != null && userInfo.getUser().getUsrId() != null) {
                UserDto userDto = getModelService().getUserDto(userInfo.getUser().getUsrId());
                if (userDto != null) {
                    String jwt = JwtUtils.generateToken(new JwtUtils.UserInfo(userDto.getId(), userDto.getUsername(), userDto.getTokenSecret()));
                    LoginResp loginResp = new LoginResp();
                    loginResp.setUser(userDto);
                    loginResp.setToken(jwt);
                    return ServiceResult.success(loginResp);
                }
            }
            UserRequest userRequest = this.convert2UserRequest(userInfo);
            UserModel userModel = getModelService().add(userRequest);
            if (userModel != null) {
                String jwt = JwtUtils.generateToken(new JwtUtils.UserInfo(userModel.getId(), userModel.getUsername(), userModel.getTokenSecret()));
                LoginResp loginResp = new LoginResp();
                loginResp.setUser(userModel);
                loginResp.setToken(jwt);
                return ServiceResult.success(loginResp);
            }
        }
        return ServiceResult.fail("login failed");
    }

    private UserRequest convert2UserRequest(BaseUserInfo userInfo) {
        UserRequest userRequest = new UserRequest();
        if (userInfo.getUserName() != null) {
            userRequest.setUsername(userInfo.getUserName());
        }
        if (userInfo.getUser() != null) {
            User user = userInfo.getUser();
            if (user.getDescription() != null) {
                userRequest.setDescription(user.getDescription());
            }
            if (user.getPwd() != null) {
                userRequest.setPassword(PasswordUtils.desEncrypt(user.getPwd()));
            }
            if (user.getUsrId() != null) {
                userRequest.setId(user.getUsrId());
            }
            if (user.getEmailBox() != null) {
                userRequest.setEmail(user.getEmailBox());
            }
            if (user.getDetailAddress() != null) {
                userRequest.setAddress(user.getDetailAddress());
            }
        }
        userRequest.setOrgIds(Arrays.asList(defaultOrg));
        userRequest.setRoleIds(Arrays.asList(defaultRole));
        return userRequest;
    }
}
