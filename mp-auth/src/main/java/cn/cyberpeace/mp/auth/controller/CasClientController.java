package cn.cyberpeace.mp.auth.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.CasServiceUtil;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.auth.util.XmlUtils;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * CAS单点登录客户端登录认证
 * </p>
 *
 * @Author zhoujf
 * @since 2018-12-20
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/cas/client")
public class CasClientController {

	@Autowired
	private UserService userService;

	// todo 需要获取的配置项
	private String prefixUrl = "https://cas.8f8.com.cn:8443/cas";

	/**
	 * 验证 SSO
	 *
	 * @param ticket   cas传过来的token
	 * @param service  登录后跳转的靶场地址
	 */
	@ApiOperation(value = "校验cas登录")
	@GetMapping("/validateLogin")
	public ServiceResult<JSONObject> authenticateSso(@RequestParam(name = "ticket") String ticket,
			@RequestParam(name = "service") String service, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			// 从cas获取用户信息
			String validateUrl = prefixUrl + "/p3/serviceValidate";
			String res = CasServiceUtil.getStValidate(validateUrl, ticket, service);
			final String error = XmlUtils.getTextForElement(res, "authenticationFailure");
			if (StringUtils.isBlank(res) || StringUtils.isNotEmpty(error)) {
				log.error("cas validation failed, ticket={}, service={}, res={}, error={}", ticket,
						service, res, error);
				throw new MpRuntimeException(AuthErrorCode.AUTHENTICATION_FAILED);
			}
			// 获取username
			final String principal = XmlUtils.getTextForElement(res, "user");
			if (StringUtils.isEmpty(principal)) {
				log.error("get empty username from cas, xml_res={}", res);
				throw new MpRuntimeException(AuthErrorCode.USERNAME_EMPTY);
			}
			// 查询用户
			UserModel userModel = userService.getByUserName(principal);
			// 用户不存在则创建
			if (null == userModel) {
				log.info("user not found, username:{}, will be registered", principal);
				// todo 需要确认是否需要用户名称，以及如何获取用户名称
				userService.createCasUser(principal, principal);
				userModel = userService.getByUserName(principal);
			}
			// 生成jwt token
			String newJwt = JwtUtils.generateToken(new JwtUtils.UserInfo(userModel.getId(),
					userModel.getUsername(), userModel.getTokenSecret()));
			// 生成返回 todo 具体的返回格式需要调试
			JSONObject obj = new JSONObject();
			obj.put("token", newJwt);
			obj.put("userInfo", userModel);
			return ResultCommon.success(obj);

		} catch (Exception e) {
			log.error("failed to authenticate", e);
			throw new MpRuntimeException(AuthErrorCode.AUTHENTICATION_FAILED);
		}
	}

}
