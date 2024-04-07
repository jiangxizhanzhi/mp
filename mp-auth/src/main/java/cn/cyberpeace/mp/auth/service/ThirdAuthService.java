package cn.cyberpeace.mp.auth.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ThirdAuthService {

    void loginByToken(String token, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    void logout(String userId);
}
