package cn.cyberpeace.mp.frontadmin.service.sys;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysConfigModel;

import java.io.File;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public interface SysConfigService extends BaseModelService<SysConfigModel, QueryParams> {

    List<SysConfigModel> listAll();

    SysConfigModel getByKeyName(String keyName);

    boolean updateByKeyName(SysConfigModel sysConfigModel);

    boolean batchUpdateByKeyname(List<SysConfigModel> configs);

    /**
     * 是否需要审核 true（1）:需要 false（0）:不需要
     * @return
     */
    boolean isNewUserNeedAudit();

    void publishConfigUpdateEvent(List<SysConfigModel> configs);


    int getFlowPersistenceTimeConfig();

/**
 * 获取流量持久化时间戳类型配置的方法
 * 该方法用于返回当前系统配置的流量持久化时间戳类型
 *
 * @return 返回一个整型值，表示当前配置的流量持久化时间戳类型
 */
    String getFlowPersistenceTimestamp();


    File compressFile(File originFile);



}
