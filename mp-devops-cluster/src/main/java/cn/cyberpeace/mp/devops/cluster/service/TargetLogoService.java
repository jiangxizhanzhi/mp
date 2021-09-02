package cn.cyberpeace.mp.devops.cluster.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TargetLogoService {

    /**
     * 初始化默认logo
     * @return
     */
    List<String>  upload();

    /**
     * 上传Logo
     * @param file
     * @return
     */
    String uploadLogo(MultipartFile file);

    /**
     * 批量保存图标
     * @return
     */
    int batchAdd(List<String>  urlList);

    /**
     * 获取默认logo数据
     * @return
     */
    List<String> list();
}
