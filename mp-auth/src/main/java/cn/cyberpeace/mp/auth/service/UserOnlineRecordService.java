package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.UserOnlineRecordModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public interface UserOnlineRecordService extends BaseModelService<UserOnlineRecordModel, QueryParams> {

    PageInfo getOnlineUserPage();

    List<UserOnlineRecordModel> listOnlined(String userId);

    List<UserOnlineRecordModel> listOfflined(String userId);

}
