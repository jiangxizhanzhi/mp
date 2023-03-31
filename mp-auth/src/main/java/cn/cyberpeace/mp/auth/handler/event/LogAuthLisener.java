package cn.cyberpeace.mp.auth.handler.event;

import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetails;
import cn.cyberpeace.mp.auth.model.ClientDetailsModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.log.event.LogAuth;
import cn.cyberpeace.mp.log.event.LogAuthEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.handler.event
 * @Author: hhuang
 * @CreateTime: 2023/03/31  14:19
 * @Description: 日志模块使用auth功能事件监听器
 * @Version: 1.0
 */
@Component
@Slf4j
public class LogAuthLisener {

    @EventListener
    public void onLogAuthEventPublished(LogAuthEvent logAuthEvent) {
        //从上下文中获取用户信息
        UserModel user = getLogContextUser(logAuthEvent.getUsercontext());
        if(Objects.isNull(user)){
            return;
        }
        //非试用账号则不做任何处理
        if(!UserUtils.isUserTrailAccount(user.getId())){
            return;
        }
        //获取同组织的所有用户id
        Set<String> sameOrgUserIdSet = UserUtils.getSameOrgUserIds(user.getId());
        String[] sameOrgUserIds = sameOrgUserIdSet.stream().toArray(String[]::new);
        Object source = logAuthEvent.getSource();
        //回传数据
        if(source instanceof LogAuth){
            LogAuth logAuth = (LogAuth)logAuthEvent.getSource();
            logAuth.setUserIds(sameOrgUserIds);
        }
        logAuthEvent.getLatch().countDown();
    }


    /**
     * @description: 通过上下文获取用户模型信息
     * @param usercontext
     * @return UserModel
     */
    private UserModel getLogContextUser(SecurityContext usercontext){
        UserModel user = null;
        Authentication authentication = usercontext.getAuthentication();
        if (Objects.isNull(authentication)) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthUserDetails) {
            user = ((AuthUserDetails) principal).getUser();
        }
        if (principal instanceof ClientDetailsModel) {
            user = null;
        }
        return user;
    }
}
