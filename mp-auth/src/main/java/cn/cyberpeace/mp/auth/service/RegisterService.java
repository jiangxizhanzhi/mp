package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.TenantUserModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.auth.request.register.RegisterRequest;
import cn.cyberpeace.mp.auth.service.impl.UserCreateCheckService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
public class RegisterService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserCreateCheckService createCheckService;

    @Autowired
    private AdminConfigService adminConfigService;

    @Autowired
    private UserCacheService userCacheService;

    @Autowired
    private TenantUserService tenantUserService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;

    // 外部平台ip配置文件读取
    @Value("${project.setting.4a-service-url:http://ip:port}")
    private String YIDONG_IP_CONFIG;

    @Transactional(rollbackFor = Exception.class)
    public ObjServiceResult<UserModel> register(RegisterRequest req) {

        // 查看系统设置，是否开放注册
        if (!adminConfigService.isPublicRegisterAvailable()) {
            log.info("public registration not available...");
            throw new MpRuntimeException(AuthErrorCode.REGISTER_NOT_AVAILABLE);
        }

        String userId = TextUtils.uuid();
        UserModel user = UserModel.builder()
                .id(userId)
                .username(req.getUsername())
                .password(req.getPassword())
                .email(req.getEmail())
                .nickname(req.getNickname())
                .name(req.getName())
                .avatar(req.getAvatar()).build();

        createCheckService.check(user);

        // 查看系统设置，注册用户是否需要审核
        if (adminConfigService.isNewUserNeedAudit()) {
            // 需要审核，设置待审核状态
            log.info("need registration audit...");
            user.setIsAudited(UserModel.Status.NOT_AUDITED);
        } else {
            // 不需要审核，设置默认角色
            String roleId = adminConfigService.getRegisterDefaultRoleId();
            RoleModel roleModel = null;
            if (StringUtils.isNotBlank(roleId)) {
                ObjServiceResult<RoleModel> result = roleService.get(roleId);
                roleModel = result.getSource().getObj();
            }

            if (Objects.isNull(roleModel)) {
                log.error("register default role:{} not exist!", roleId);
                throw new MpRuntimeException(AuthErrorCode.REGISTER_USER_DEFAULT_ROLE_NOT_EXISTED);
            }

            UserRoleModel userRoleModel = new UserRoleModel();
            userRoleModel.setRole(roleId);
            userRoleModel.setUser(userId);
            userRoleService.create(userRoleModel);
        }

        ObjServiceResult<UserModel> result = userService.create(user);
        if (Objects.nonNull(result.getSource().getObj())) {
            UserModel userModel = result.getSource().getObj();
            userId = userModel.getId();
        } else {
            log.error("create user error!");
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }

        TenantUserModel tenantUserModel = TenantUserModel.builder().user(userId).org(AuthConstant.ROOT_ORG_ID).build();
        ObjServiceResult<TenantUserModel> re = tenantUserService.create(tenantUserModel);
        ResultUtils.throwExceptionIfError(re, AuthErrorCode.USER_ORG_ADD_ERROR);

        userCacheService.evictAllUserCache();
        return result;
    }

    private String getRequest(String url, String token) {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet httpget = new HttpGet(url);
            httpget.addHeader("Authorization", token);
            httpget.addHeader("Content-Type", "application/json");
            HttpResponse resp = client.execute(httpget);
            // 设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
            httpget.setConfig(requestConfig);
            // 获得返回值
            String result = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
            return result;
        } catch (Exception e) {
            throw new MpRuntimeException(e.getMessage());
        }
    }

    private String postRequest(String url, Map<String, Object> formMap, String token) {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost httppost = new HttpPost(url);
            httppost.addHeader("Authorization", token);
            httppost.addHeader("Content-Type", "application/json");
            httppost.setEntity(new StringEntity(JsonUtils.stringify(formMap), StandardCharsets.UTF_8));
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
            httppost.setConfig(requestConfig);
            HttpResponse resp = client.execute(httppost);
            // 获得返回值
            String result = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
            return result;
        } catch (Exception e) {
            throw new MpRuntimeException(e.getMessage());
        }
    }

}
