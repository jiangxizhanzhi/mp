package cn.cyberpeace.mp.datasandtable.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.datasandtable.dao.WazuhDao;
import cn.cyberpeace.mp.datasandtable.model.FileEventModel;
import cn.cyberpeace.mp.datasandtable.model.WazuhFileEventRequest;
import cn.cyberpeace.mp.datasandtable.model.WazuhFileRoot;
import cn.cyberpeace.mp.datasandtable.model.WindowsEventModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * wazuh对接
 */
@Service
public class WazuhService {

    @Autowired
    private WazuhDao wazuhDao;

    /**
     * 查询文件事件分页
     * @param wazuhFileEventRequest
     * @return
     */
    public ListObjResult<FileEventModel> queryFileEvents(WazuhFileEventRequest wazuhFileEventRequest){
        String requestUri = "";
        WazuhFileRoot wazuhFileRoot = wazuhDao.queryFileEvents(requestUri);
        return ListObjResult.<FileEventModel>builder().total(wazuhFileRoot.getData().getTotalAffectedItems()).build();
    }

    /**
     * 查询windows注册表事件分页
     * @return
     */
    public ListObjResult<WindowsEventModel> queryWindowsRegistryEvents(){
        String requestUri = "";
        WazuhFileRoot wazuhFileRoot = wazuhDao.queryFileEvents(requestUri);
        return ListObjResult.<WindowsEventModel>builder().build();
    }
}
