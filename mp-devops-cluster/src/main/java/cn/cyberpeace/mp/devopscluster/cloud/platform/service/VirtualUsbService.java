package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.VirtualUsbStorage;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.request.VirtualUsbDeleteRequest;
import cn.cyberpeace.mp.devopscluster.request.queryparam.VirtualUsbQueryParams;
import cn.cyberpeace.mp.devopscluster.vo.VirtualUsbVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Description: 虚拟USB服务
 * @Author: guquan
 * @Date：2023/4/17 10:27
 */
public interface VirtualUsbService {

    /**
     * 分页查询虚拟USB列表
     * @param queryParams
     * @return
     */
    ListObjServiceResult<VirtualUsbVO> virtualUsbPage(VirtualUsbQueryParams queryParams);

    /**
     * 根据uuid查询虚拟USB信息
     * @param uuid
     * @return
     */
    VirtualUsbVO findByUuid(String uuid);

    /**
     * 新增虚拟USB信息
     * @param virtualUsbVO
     */
    void add(VirtualUsbVO virtualUsbVO);

    /**
     * 编辑虚拟USB信息
     * @param virtualUsbVO
     */
    void update(VirtualUsbVO virtualUsbVO);

    /**
     * 删除虚拟USB信息
     * @param request
     */
    void delete(VirtualUsbDeleteRequest request);

    /**
     * 根据UUID查询虚拟USB文件列表
     * @param uuid
     * @return
     */
    VirtualUsbStorage fileListByUuid(String uuid);

    /**
     * 上传文件到虚拟USB
     * @param files
     * @param uuid
     * @return
     */
    BaseCloudPlatformResult upload(List<MultipartFile> files, String uuid);

    /**
     * 虚拟USB删除文件
     * @param uuid
     * @param fileName
     */
    void deleteFile(String uuid, String fileName);

    /**
     * 虚拟USB格式化
     * @param uuid
     */
    void deleteAllFiles(String uuid);

    /**
     * 虚拟USB下载文件
     * @param uuid
     * @param fileName
     * @return
     */
    String downloadFile(String uuid, String fileName);
}
