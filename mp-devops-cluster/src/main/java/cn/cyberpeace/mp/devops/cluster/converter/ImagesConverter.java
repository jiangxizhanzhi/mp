package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.converter.model.ImageAuthConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.ImageConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.ImageConvertPageModel;
import cn.cyberpeace.mp.devops.cluster.request.*;

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
    void update(ImageRequest request);

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
    void delete(ImagesDeleteRequest request);

    /**
     * 授权
     */
    void auth(ImagesAuthRequest request);

    /**
     * 导出
     */
    void export(ImagesExportRequest request);

}
