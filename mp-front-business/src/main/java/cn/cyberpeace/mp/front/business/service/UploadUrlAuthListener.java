package cn.cyberpeace.mp.front.business.service;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.front.business.dto.ImportUrlAuth;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 模板的读取类
 * 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Slf4j
public class UploadUrlAuthListener extends AnalysisEventListener<ImportUrlAuth> {

    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 10000;

    /**
     * 最大一次性导入100条数据
     */
    private static final int MAX_COUNT = 10000;

    List<ImportUrlAuth> list = new ArrayList<>();

    //private TargetService targetService;

    private UrlACService urlACService;

    //private OperateUserPermissionService opUserPermCheckService;

    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     *
     * @param userService
     */
    //public UploadUrlAuthListener(UserService userService, RoleService roleService, OrgService orgService, OperateUserPermissionService opUserPermCheckService) {
    //    log.info("New UploadDataListener Instance...");
    //    this.userService = userService;
    //    this.roleService = roleService;
    //    this.orgService = orgService;
    //    this.opUserPermCheckService = opUserPermCheckService;
    //}

    public UploadUrlAuthListener(UrlACService urlACService) {
        this.urlACService = urlACService;
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data
     *            one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(ImportUrlAuth data, AnalysisContext context) {
        log.info("got one data:{}", JSON.toJSONString(data));
        list.add(data);

        if (list.size() > MAX_COUNT) {
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_SIZE_OVER.format("100"));
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
     * http://10.10.10.10/biz/bug-view-4861.html
     *
     * 当前导入模板里面说明的必填项是用户名、姓名、密码， 其他的组织、角色、电话号码没有在导入模板中说必填，
     * 组织不填的话导入后要有默认值。
     * 组织：可以不填， 不填的话默认根组织
     * 角色：必填， 属于已有角色可以导入，不属于已有角色导入失败。
     */
    private void saveData() {
        log.info("list size:{}！", list.size());

        //List<TargetModel> allTargets = targetService.listAll(AuthConstant.DEFAULT_TENANT, null);
        //List<String> targetIds = allTargets.stream().map(TargetModel::getId).collect(Collectors.toList());
        //List<ImportUrlAuth> list1 = list.stream().filter(u -> !targetIds.contains(u.getMenuId())).collect(Collectors.toList());
        // 校验字段是否合法
        //checkImportUsers(list);
        //
        //userService.importExcel(list);
        urlACService.saveUrlAccessCtl(list);


        log.info("save success！");
    }



}
