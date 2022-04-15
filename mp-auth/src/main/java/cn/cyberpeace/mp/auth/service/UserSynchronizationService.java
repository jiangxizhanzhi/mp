package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.dto.user.HLDUserDto;
import cn.cyberpeace.mp.auth.dto.user.HLDUserResponse;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.dto.user.UserRequest;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public static final String defaultOrg = "a13262d600fe40c799d63dce5c1485af";

    public static final String defaultRole = "5827ff7847504ffd806e0a85c417228d";

    public static final String defaultPassword = "Dx72000000";

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
        String hldUsers = null;
        HLDUserResponse response = new HLDUserResponse();
        try {
            hldUsers = HttpUtils.sendPost(hldUsersUrl + "/xinan-server/mhUser/getAllUserNopage", "null");
        } catch (IOException e) {
            log.error("get HLDUsers error,msg:{}", e);
        }
//        hldUsers = hld;
        if (hldUsers == null) {
            log.error("get HLDUsers failed");
            return;
        }
        response = JsonCommon.parse(hldUsers, HLDUserResponse.class);
        List<String> userIds = new ArrayList<>();
        if (response == null) {
            return;
        }
        response.getInfo().forEach(in -> {
            if (in == null) {
                log.error("this user is null");
                return;
            }
            if (in.getId() == null) {
                log.error("this userId is null:{}", in);
                return;
            }
            String userId = in.getId().toString();
            userIds.add(in.getId().toString());
            UserDto user = getModelService().getUserDto(userId);
            boolean needUpdate = this.checkUpdate(user, in);
            if (user == null) {
                UserRequest createRequest = HLDUser2UserRequest(in, true);
                getModelService().add(createRequest);
            } else if (needUpdate) {
                UserRequest modifyRequest = HLDUser2UserRequest(in, false);
                getModelService().modify(modifyRequest);
            }
        });
        List<UserModel> userModels = new ArrayList<>();
        userModels = userService.queryAllUsers();
        userModels.forEach(user -> {
            if (!userIds.contains(user.getId()) && !user.getId().equals(AuthConstant.ROOT_USER_ID)) {
                log.info("delete user:{}", user.getId());
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

    private UserRequest HLDUser2UserRequest(HLDUserDto hldUserDto, boolean changePassword) {
        UserRequest userRequest = new UserRequest();
        if (hldUserDto.getId().toString() != null) {
            userRequest.setId(hldUserDto.getId().toString());
        }
        if (hldUserDto.getAccount() != null) {
            userRequest.setUsername(hldUserDto.getAccount());
        }
        if (hldUserDto.getName() != null) {
            userRequest.setName(hldUserDto.getName());
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
        if (changePassword) {
            userRequest.setPassword(PasswordUtils.desEncrypt(defaultPassword));
        }
        userRequest.setOrgIds(Arrays.asList(defaultOrg));
        userRequest.setRoleIds(Arrays.asList(defaultRole));
        userRequest.setInitCheck(1);
        return userRequest;
    }

    private boolean checkUpdate(UserDto userDto, HLDUserDto hldUserDto) {
        if (userDto == null) {
            return false;
        }
        if (hldUserDto.getAccount() != null && !hldUserDto.getAccount().equals(userDto.getUsername())) {
            return true;
        }
        if (hldUserDto.getName() != null && !hldUserDto.getName().equals(userDto.getName())) {
            return true;
        }
        if (hldUserDto.getBirthday() != null && !hldUserDto.getBirthday().equals(userDto.getBirthday())) {
            return true;
        }
        if (hldUserDto.getE_mail() != null && !hldUserDto.getE_mail().equals(userDto.getEmail())) {
            return true;
        }
        if (hldUserDto.getIdCard() != null && !hldUserDto.getIdCard().equals(userDto.getIdCard())) {
            return true;
        }
        if (hldUserDto.getMhDescribe() != null && !hldUserDto.getMhDescribe().equals(userDto.getDescription())) {
            return true;
        }
        if (hldUserDto.getMobilePhone() != null && !hldUserDto.getMobilePhone().equals(userDto.getMobile())) {
            return true;
        }
        if (hldUserDto.getPostalAddress() != null && !hldUserDto.getPostalAddress().equals(userDto.getAddress())) {
            return true;
        }
        return false;
    }
}
