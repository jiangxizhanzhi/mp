package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.repository.AdminMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class AdminConfigService {

    private static final String KEY_PUBLIC_REGISTER = "public_register";

    private static final String KEY_USER_AUDIT = "user_audit";

    private static final String KEY_REGISTER_USER_DEFAULT_ROLE = "register_user_default_role";

    @Autowired
    private AdminMapper adminMapper;

    
    public boolean isPublicRegisterAvailable() {
        String value = adminMapper.getConfigValue(KEY_PUBLIC_REGISTER);
        if (StringUtils.isNotBlank(value) && Objects.equals("1", value)) {
            
            return true;
        }
        
        return false;
    }

    
    public boolean isNewUserNeedAudit() {
        String value = adminMapper.getConfigValue(KEY_USER_AUDIT);
        if (StringUtils.isNotBlank(value) && Objects.equals("1", value)) {
            
            return true;
        }
        return false;
    }


    
    public String getRegisterDefaultRoleId() {
        String value = adminMapper.getConfigValue(KEY_REGISTER_USER_DEFAULT_ROLE);
        return value;
    }

}
