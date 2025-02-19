package cn.cyberpeace.mp.frontbusiness.controller.register;


import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.request.register.RegisterRequest;
import cn.cyberpeace.mp.auth.service.RegisterService;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.auth.util.KaptchaUtil;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.constant.LogConstant;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @Description: 注册接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "注册接口", tags = "AUTH-注册接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/register")
public class RegisterController {

    @Autowired
    private RegisterService registerService;
    @Autowired
    private RestTemplate restTemplate;

    @ApiOperation(value = "注册接口", notes = "")
    @ApiImplicitParam(name = "req", value = "注册请求体", dataTypeClass = RegisterRequest.class, required = true, dataType = "body")
    @PostMapping
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.CREATE, title = "注册新用户", target = OpTargetEnums.USER)
    public ServiceResult<UserModel> register(@RequestBody @Validated RegisterRequest req) {
        ServiceResult<Object> checkResult = KaptchaUtil.checkKaptcha(req.getVerifyCode());
        if (Boolean.FALSE.equals(checkResult.getSource())) {
            throw new MpRuntimeException(checkResult.getServiceCode(), checkResult.getDetail());
        }
        String encryptedPwd = req.getPassword();
        req.setPassword(PasswordUtils.desDecrypt(encryptedPwd));
        ObjServiceResult<UserModel> result = registerService.register(req);

        if (result.getSource() != null) {
            UserModel userModel = result.getSource().getObj();
            JwtUtils.addResponseHeaderWithJWT(userModel);
            HttpServletRequest request = ServletUtils.getRequest();
            request.setAttribute(LogConstant.USER_ID_KEY, userModel.getId());
            request.setAttribute(LogConstant.USER_NAME_KEY, userModel.getUsername());
            request.setAttribute(LogConstant.USER_TYPE_KEY,  LogConstant.USER_TYPE_USER);
            return ResultCommon.success(userModel);
        }

        throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
    }


    @ApiOperation(value = "测试接口")
    @PostMapping("/test/remote_access/api/session/tunnels/{tunnels}/streams/{type}/{file}")
    public ServiceResult<String> test(@PathVariable("tunnels") String tunnels, @PathVariable("type") String type, @PathVariable("file") String file, @RequestParam("token") String token, HttpServletRequest httpServletRequest) throws IOException {
        // 获取请求头
        HttpHeaders headers = new HttpHeaders();
        httpServletRequest.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            headers.add(headerName, httpServletRequest.getHeader(headerName));
        });

        // 获取请求体
        InputStream reader = httpServletRequest.getInputStream();
        byte[] body = {};
        if (reader.available() > 0) {
            body = reader.readAllBytes();
        }

        // 创建新的请求体 (可以选择是否与原请求体相同)
        HttpEntity<byte[]> entity = new HttpEntity<>(body, headers);
        String url = String.format("http://10.100.12.1:9200/guacamole/api/session/tunnels/%s/streams/%s/%s?token=%s", tunnels, type, file, token);
        var resp = restTemplate.postForEntity(url,
                entity, HashMap.class, Collections.emptyMap());
        log.info("[guacamole] response: {}", JsonCommon.stringify(resp));
        return new ServiceResult<>("").asResult();
    }

}
