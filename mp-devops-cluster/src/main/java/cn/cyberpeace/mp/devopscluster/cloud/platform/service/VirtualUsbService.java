package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.VirtualUsbStorage;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.request.VirtualUsbDeleteRequest;
import cn.cyberpeace.mp.devopscluster.request.queryparam.VirtualUsbQueryParams;
import cn.cyberpeace.mp.devopscluster.vo.VirtualUsbVO;
import org.springframework.web.multipart.MultipartFile;


public interface VirtualUsbService {

    
    ListObjServiceResult<VirtualUsbVO> virtualUsbPage(VirtualUsbQueryParams queryParams);

    
    VirtualUsbVO findByUuid(String uuid);

    
    void add(VirtualUsbVO virtualUsbVO);

    
    void update(VirtualUsbVO virtualUsbVO);

    
    void delete(VirtualUsbDeleteRequest request);

    
    VirtualUsbStorage fileListByUuid(String uuid);

    
    BaseCloudPlatformResult upload(MultipartFile file, String uuid);

    
    void deleteFile(String uuid, String fileName);

    
    void deleteAllFiles(String uuid);

    
    String downloadFile(String uuid, String fileName);
}
