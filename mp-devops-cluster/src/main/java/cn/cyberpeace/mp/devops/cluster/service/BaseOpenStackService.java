package cn.cyberpeace.mp.devops.cluster.service;

/**
 * 基础对接云平台接口
 * @author wanggang
 */
public interface BaseOpenStackService<T> {

    /**
     * 分页查询
     * @return
     */
    T page();
}
