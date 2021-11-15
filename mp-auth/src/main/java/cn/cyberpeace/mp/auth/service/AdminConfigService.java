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

    private static final String KEY_USER_AUDIT = "user_audit";

    @Autowired
    private AdminMapper adminMapper;

    public boolean isNewUserNeedAudit() {
        String value = adminMapper.getConfigValue(KEY_USER_AUDIT);
        if (StringUtils.isNotBlank(value) && Objects.equals("1", value)) {
            // 新用户需要审核
            return true;
        }
        return false;
    }

}
