package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.user.*;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.param.UserQueryParams;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.util.HttpUtils;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * @Description:
 * @Author hewei
 * @Date：2021/2/17 11:11
 */
@Service
@Slf4j
public class UserSynchronizationService extends BaseModelController<UserModel, UserQueryParams, UserService> {

    @Autowired
    public UserSynchronizationService(UserService modelService) {
        setModelService(modelService);
    }

    @Autowired
    private UserService userService;

    @Value("${mp.hld.users-url}")
    private String hldUsersUrl;

    private final String hldUrl = hldUsersUrl + "/xinan-server/mhUser/getAllUserNopage";

    public static final String defaultOrg = "a13262d600fe40c799d63dce5c1485af";

    public static final String defaultRole = "5827ff7847504ffd806e0a85c417228d";

    //    public static final String hld = "{\"msg\":\"成功\",\"code\":200,\"info\":[{\"id\":1,\"userName\":\"heweitest007\",\"name\":\"heweitest007\",\"passWord\":\"12345678\"},{\"id\":2,\"userName\":\"heweitest008\",\"name\":\"heweitest008\",\"passWord\":\"12345678\"}]}";

    // 每2分钟统计一次
    public static final long REFRESH_RATE = 2;

    public static final long REFRESH_MINUTE = 60000;

    private static ExecutorService threadPool = null;

    @Scheduled(initialDelay = REFRESH_RATE * REFRESH_MINUTE, fixedRate = REFRESH_RATE * REFRESH_MINUTE)
    public void scheduleUser() {
        if (threadPool == null) {
            threadPool = getSingletonThreadPool();
        }
        String hldUsers = "";
        HLDUserResponse response = new HLDUserResponse();
        try {
            hldUsers = HttpUtils.sendPost(hldUrl, "null");
        } catch (IOException e) {
            log.error("get HLDUsers error,msg:{}", e);
        }
//        hldUsers = hld;
        response = JsonCommon.parse(hldUsers, HLDUserResponse.class);
        List<String> userIds = new ArrayList<>();
        if (response == null) {
            return;
        }
        response.getInfo().forEach(in -> {
            if (in.getId().toString() == null) {
                return;
            }
            String userId = in.getId().toString();
            userIds.add(in.getId().toString());
            if (getModelService().getUserDto(userId) == null) {
                UserRequest createRequest = HLDUser2UserRequest(in);
                getModelService().add(createRequest);
            } else if (!in.getUserUpdateTime().equals(getModelService().getUserDto(userId).getUpdateTime())) {
                UserRequest modifyRequest = HLDUser2UserRequest(in);
                getModelService().modify(modifyRequest);
            }
        });
        List<UserModel> userModels = new ArrayList<>();
        userModels = userService.queryAllUsers();
        userModels.forEach(user -> {
            if (!userIds.contains(user.getId()) || !user.getId().equals("3f655d87112f4ae094bbaf70830e20aa")) {
                getModelService().delete(user.getId());
            }
        });
    }

    /**
     * 静态内部类的单例模式
     *
     * @return
     */
    public static ExecutorService getSingletonThreadPool() {
        return ThreadPoolHolder.threadPoolExecutor;
    }

    private static class ThreadPoolHolder {
        static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("online-report-pool-%d").build();
        private static ExecutorService threadPoolExecutor = new ThreadPoolExecutor(20, 100, 20, TimeUnit.SECONDS,
                new ArrayBlockingQueue(100), namedThreadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    private UserRequest HLDUser2UserRequest(HLDUserDto hldUserDto) {
        UserRequest userRequest = new UserRequest();
        if (hldUserDto.getId().toString() != null) {
            userRequest.setId(hldUserDto.getId().toString());
        }
        if (hldUserDto.getUserName() != null) {
            userRequest.setUsername(hldUserDto.getUserName());
        }
        if (hldUserDto.getName() != null) {
            userRequest.setName(hldUserDto.getName());
        }
        if (hldUserDto.getPassWord() != null) {
            userRequest.setPassword(PasswordUtils.desEncrypt(hldUserDto.getPassWord()));
        }
        if (hldUserDto.getIdCard() != null) {
            userRequest.setIdCard(hldUserDto.getIdCard());
        }
        if (hldUserDto.getBirthday() != null) {
            userRequest.setBirthday(hldUserDto.getBirthday());
        }
        if (hldUserDto.getMobilePhone() != null) {
            userRequest.setMobile(hldUserDto.getMobilePhone());
        }
        if (hldUserDto.getE_mail() != null) {
            userRequest.setEmail(hldUserDto.getE_mail());
        }
        if (hldUserDto.getPostalAddress() != null) {
            userRequest.setAddress(hldUserDto.getPostalAddress());
        }
        if (hldUserDto.getMhDescribe() != null) {
            userRequest.setDescription(hldUserDto.getMhDescribe());
        }
        if (hldUserDto.getUserUpdateTime() != null) {
            userRequest.setUpdateTime(hldUserDto.getUserUpdateTime());
        }
        userRequest.setOrgIds(Arrays.asList(defaultOrg));
        userRequest.setRoleIds(Arrays.asList(defaultRole));
        return userRequest;
    }
}
