package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.constant.AuthLogConstant;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.devops.log.constant.LogConstant;
import cn.cyberpeace.mp.devops.log.enums.SiteTypeEnums;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import cn.cyberpeace.mp.devops.log.service.OperateLogServiceFactory;
import cn.cyberpeace.mp.devops.log.utils.SnUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class LogService {

    @Autowired
    private OperateLogServiceFactory operateLogServiceFactory;

    public void record(UserModel user) {
        if (Objects.isNull(user)) {
            return;
        }

        OperationLogModel operationLogModel = new OperationLogModel();
        //站点类型
        operationLogModel.setSiteType(SiteTypeEnums.MP);
        //操作模块
        operationLogModel.setOpModule(AuthLogConstant.MODULE);
        //操作类型
        operationLogModel.setOpType(AuthLogConstant.TYPE_LOGIN);
        //操作主题
        operationLogModel.setTitle("登录");
        //操作描述
        operationLogModel.setDescription("账号密码登录");
        //记录操作时间
        operationLogModel.setOpTime(LocalDateTime.now());

        // todo 异步处理的时候request外部传入
        HttpServletRequest request = ServletUtils.getRequest();

        //请求IP
        operationLogModel.setOpIp(SnUtils.getRealRemoteIp(request));

        //操作用
        operationLogModel.setUserId(user.getId());
        operationLogModel.setUserName(user.getUsername());
        operationLogModel.setUserType(LogConstant.USER_TYPE_USER);

        operateLogServiceFactory.create(operationLogModel);
    }



    public void getUserLog() {

    }


}
