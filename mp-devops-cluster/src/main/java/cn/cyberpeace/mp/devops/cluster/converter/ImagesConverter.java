package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.devops.cluster.converter.model.ImageAuthConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.ImageConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.ImageConvertPageModel;
import cn.cyberpeace.mp.devops.cluster.request.*;
import org.apache.http.HttpResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImagesConverter extends BaseConverter{

    /**
     * 分页查询
     * @param request
     */
    ImageConvertPageModel page(ImagePageRequest request);

    /**
     * 新增
     */
    Boolean add(ImageRequest request);

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    ImageConvertModel findById(String id);

    /**
     * 更新
     * @param request
     */
    Boolean update(ImageRequest request);

    /**
     * 预览
     * @param request
     */
    void look(ImagesLookRequest request);

    /**
     * 构建
     */
    void build(ImagesBuildRequest request);

    /**
     * 根据ID查询授权信息
     * @param imageId
     * @return
     */
    ImageAuthConvertModel findAuthById(String imageId);

    /**
     * 删除
     */
    Boolean delete(ImagesDeleteRequest request);

    /**
     * 授权
     */
    void auth(ImagesAuthRequest request);

    /**
     * 上传Logo
     * @param file
     * @return
     */
    String uploadImage(ImagesUploadRequest request ,MultipartFile file);

    /**
     * 导出
     */
    HttpResponse export(String uuid);

    /**
     * 创建虚拟机/容器
     * @param uuid
     */
    void create(String uuid);

    /**
     * 根据镜像ID查询用户列表
     * @param uuid
     * @return
     */
    List<UserDto> findUsersByUuid(String uuid);
}
