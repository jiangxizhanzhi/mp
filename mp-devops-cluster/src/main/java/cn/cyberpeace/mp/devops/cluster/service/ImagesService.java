package cn.cyberpeace.mp.devops.cluster.service;

/**
 * 镜像管理底层接口
 */
public interface ImagesService extends BaseCloudPlatformService  {

    /**
     * 分页查询
     */
    void page(String getParams);

    /**
     * 新增
     */
    void add();

    /**
     * 更新
     */
    void update();

    /**
     * 构建
     */
    void build();


    /**
     * 删除
     */
    void delete();

    /**
     * 授权
     */
    void auth();

    /**
     * 导出
     */
    void export();
}
