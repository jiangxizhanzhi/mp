package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.repository.AdminMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/6 14:21
 */
@Service
public class AdminConfigService {

    private static final String KEY_PUBLIC_REGISTER = "public_register";

    private static final String KEY_USER_AUDIT = "user_audit";

    private static final String KEY_REGISTER_USER_DEFAULT_ROLE = "register_user_default_role";

    @Autowired
    private AdminMapper adminMapper;

    /**
     * 允许用户公开注册
     * @return
     */
    public boolean isPublicRegisterAvailable() {
        String value = adminMapper.getConfigValue(KEY_PUBLIC_REGISTER);
        if (StringUtils.isNotBlank(value) && Objects.equals("1", value)) {
            // 允许注册
            return true;
        }
        // 不允许注册
        return false;
    }

    /**
     * 是否新注册用户需要审核
     * @return
     */
    public boolean isNewUserNeedAudit() {
        String value = adminMapper.getConfigValue(KEY_USER_AUDIT);
        if (StringUtils.isNotBlank(value) && Objects.equals("1", value)) {
            // 新用户需要审核
            return true;
        }
        return false;
    }


    /**
     * 查询注册用户默认角色ID
     * @return
     */
    public String getRegisterDefaultRoleId() {
        String value = adminMapper.getConfigValue(KEY_REGISTER_USER_DEFAULT_ROLE);
        return value;
    }

}
