package cn.cyberpeace.mp.auth.handler;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.constant.RoleAuthControlConstant;
import cn.cyberpeace.mp.auth.util.UserUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.handler
 * @Author: hhuang
 * @CreateTime: 2023/03/25  10:40
 * @Description: 角色处理抽象类
 * @Version: 1.0
 */
public abstract class AbstractRoleHandler implements RoleHandler{

    @Override
    public void handle(HttpServletRequest request) throws Exception{
        if(isTrailAccount()){
            handleRequest(request);
        }
    }

    /**
     * @description: 获取当前用户
     * @throws
     * @return UserBo
     */
    protected UserBo getCurrentUser(){
        return UserUtils.getLoginUserDtoWithoutOrg();
    }

    /**
     * @description: 是否为试用角色
     * @throws
     * @return boolean
     */
    protected boolean isTrailAccount(){
        var user = getCurrentUser();
        return Objects.isNull(user) ? false
                : RoleAuthControlConstant.TRAIL_TYPE.TRUE.getType().equals(user.getIsTrialAccount());
    }

    /**
     * @description: 角色请求处理基本方法
     * @return void
     */
    public abstract void handleRequest(HttpServletRequest request) throws Exception;

}
