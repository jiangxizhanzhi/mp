package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.excel.ImportUser;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 模板的读取类
 * 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Slf4j
public class UploadDataListener extends AnalysisEventListener<ImportUser> {

    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;

    /**
     * 最大一次性导入100条数据
     */
    private static final int MAX_COUNT = 100;

    List<ImportUser> list = new ArrayList<ImportUser>();
    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    private UserService userService;

    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     *
     * @param userService
     */
    public UploadDataListener(UserService userService) {
        log.info("New UploadDataListener Instance...");
        this.userService = userService;
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data
     *            one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(ImportUser data, AnalysisContext context) {
        log.info("解析到一条数据:{}", JSON.toJSONString(data));
        list.add(data);

        if (list.size() > MAX_COUNT) {
            throw new MpRuntimeException("导入数据超过100条！");
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        log.info("所有数据解析完成！");
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", list.size());

        // 校验字段是否合法
        checkImportUsers(list);

        userService.importExcel(list);
        log.info("存储数据库成功！");
    }


    private void checkImportUsers(List<ImportUser> users) {
        log.info("checkImportUsers... list size:{}", users.size());
        if (CollectionUtil.isEmpty(users)) {
            throw new MpRuntimeException("列表为空！");
        }

        for (int i = 0; i < users.size(); i++) {
            ImportUser user = users.get(i);
            int index = i + 1;
            if (Objects.nonNull(user)) {
                String errMsgPrefix = "第" + index + "条数据错误！";
                checkUserName(user.getUsername(), errMsgPrefix);
                checkName(user.getName(), errMsgPrefix);
                checkPwd(user.getPassword(), errMsgPrefix);
            }
        }
    }


    /**
     * 校验用户名
     *
     * @param username
     * @param errMsgPrefix
     */
    private void checkUserName(String username, String errMsgPrefix) {
        if (StringUtils.isBlank(username)) {
            throw new MpRuntimeException(errMsgPrefix + "用户名【" + username + "】不能为空!");
        }

        if (StringUtils.length(username) < 2 || StringUtils.length(username) > 20) {
            throw new MpRuntimeException(errMsgPrefix + "用户名【" + username + "】长度不合法！");
        }

        if (userService.getByUserName(username) != null) {
            throw new MpRuntimeException(errMsgPrefix + "用户名【" + username + "】已存在！");
        }

    }

    /**
     * 校验名称
     *
     * @param name
     * @param errMsgPrefix
     */
    private void checkName(String name, String errMsgPrefix) {
        if (StringUtils.isBlank(name)) {
            throw new MpRuntimeException(errMsgPrefix + "名称【" + name + "】不能为空!");
        }

        if (StringUtils.length(name) < 2 || StringUtils.length(name) > 40) {
            throw new MpRuntimeException(errMsgPrefix + "名称【" + name + "】长度不合法！");
        }

    }


    /**
     * 校验密码
     *
     * @param password
     * @param errMsgPrefix
     */
    private void checkPwd(String password, String errMsgPrefix) {
        // 密码是否合法
        if (PasswordUtils.isPasswordStrengthLowLevel(password)) {
            throw new MpRuntimeException(errMsgPrefix + "密码【" + password + "】强度不够!");
//            throw new MpRuntimeException(AuthErrorCode.PASSWORD_STRENGTH_INVALID);
        }
    }

}
