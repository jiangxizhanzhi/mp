package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.register.RegisterReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RegisterService {

    @Autowired
    private UserService userService;

    public void register(RegisterReq registerReq) {
        // todo
    }


}
