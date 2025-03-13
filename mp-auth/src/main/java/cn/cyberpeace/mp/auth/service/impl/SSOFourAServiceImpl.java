package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.sso.SSOLoginBO;
import cn.cyberpeace.mp.auth.constant.SSOSysEnum;
import cn.cyberpeace.mp.auth.service.AbstractSSOService;
import com.boco.iam.sso.client.LoginUtil;
import com.boco.iam.sso.client.ResultCode;
import com.boco.iam.sso.client.TransferTicket;
import com.boco.iam.sso.client.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.service.impl
 * @Author: hhuang
 * @CreateTime: 2025/03/13  16:08
 * @Description: 4a单点
 * @Version: 1.0
 */
@Slf4j
@Service
public class SSOFourAServiceImpl extends AbstractSSOService {

    @Resource
    private LoginUtil loginUtil;

    @Override
    public boolean isSupport(SSOSysEnum sysEnum) {
        return SSOSysEnum.FOUR_A == sysEnum;
    }

    @Override
    public void doLogin(SSOLoginBO ssoLoginBO) {
        HttpServletRequest request = ssoLoginBO.getRequest();
        HttpServletResponse response = ssoLoginBO.getResponse();
        if(!loginUtil.isEnable()){
            //服务不可用，认证失败
            log.warn("sso service is not available!");
            loginUtil.redirectToServer(request, response);
            return;
        }
        String errorMessage = null;
        if(loginUtil.checkTicket(request)){
            log.warn("sso request param ticket is empty!");
            loginUtil.redirectToServer(request, response);
            return;
        }
        String strTic = loginUtil.getTicket(request);
        TransferTicket ticket = loginUtil.analysTicket(strTic);
        if(ticket!=null && ResultCode.RESULT_OK.equals(ticket.getRetCode())){
            UserInfo userInfo = loginUtil.qryUserByTicket(ticket);
            if(Objects.nonNull(userInfo) && ResultCode.RESULT_OK.equals(userInfo.getRetCode())){
                //应用资源根据帐号信息做登录后业务处理 todo；用户信息入库，并进入指定页面或者首页
            }else {
                //用户不存在或者其他非成功的情况，todo 只有4a单点则重定向到登录页
            }
        }else{
            //根据错误信息提示用户 todo；
            errorMessage = loginUtil.getCodeMess(ticket.getRetCode(),
                request.getLocale().getLanguage());

        }
        return ;
    }

    @Override
    public void doLogout() {

    }
}
