package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.target.TargetBo;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.util.List;

/**
 * @ClassName TargetService
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface TargetService extends BaseModelService<TargetModel, QueryParams> {

    /**
     * exists
     *
     * @param id
     * @return boolean
     */
    boolean exists(String id);

    /**
     * listByParent
     *
     * @param parentId
     * @return
     */
    <T extends TargetModel> List<T> listByParent(String parentId);

    /**
     * 查询所有资源，visible=1
     *
     * @param tenant
     * @param type
     * @return
     */
    <T extends TargetModel> List<T> listAll(String tenant, Integer type);

    /**
     * 查询所有菜单，忽略visible=1的限制
     *
     * @param tenant
     * @param type
     * @return
     */
    <T extends TargetModel> List<T> listAllIgnoreVisible(String tenant, Integer type);

    /**
     * getTree
     *
     * @param parentId
     * @param type
     * @return
     */
    List<TargetBo> getTree(String parentId, Integer type);

    /**
     * deleteAll
     *
     * @return boolean
     */
    boolean deleteAll();
}
