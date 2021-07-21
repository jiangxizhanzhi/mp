package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.user.UserOlReportCntDto;
import cn.cyberpeace.mp.auth.dto.user.UserOnlineReportPageRequest;
import cn.cyberpeace.mp.auth.model.UserOnlineReportModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import com.github.pagehelper.PageInfo;

public interface UserOnlineReportService extends BaseModelService<UserOnlineReportModel, QueryParams> {

    PageInfo page(UserOnlineReportPageRequest req);

    boolean createOrUpdate(UserOlReportCntDto userOlReportCntDto);

}
