package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.request.*;

public interface ImagesConverter {

    /**
     * 分页查询
     * @param request
     */
    void page(ImagesPageRequest request);

    /**
     * 新增
     */
    void add(ImagesAddRequest request);

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
