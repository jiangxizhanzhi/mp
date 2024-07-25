package cn.cyberpeace.mp.cscene.core.base;

import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneModel;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneRoleModel;
import lombok.Getter;

import java.util.List;


/**
 * 综合场景处理
 */
@Getter
public abstract class BaseCsceneHandler<
        TCsceneModel extends BaseCsceneModel,
        TCsceneRoleModel extends BaseCsceneRoleModel> {

    private CsceneContextBO context;

    protected boolean initLazy;

    /** 综合场景id */
    private String csceneId;


    /**
     * 从数据库读取综合场景信息
     * @return 综合场景信息
     */
    protected abstract TCsceneModel readCscene();

    /**
     * 从数据库读取综合场景角色信息
     * @return 综合场景角色信息
     */
    public abstract List<TCsceneRoleModel> readCsceneRoles();

    /**
     * 重新载入综合场景数据
     * @return 载入是否成功
     */
    public abstract boolean reloadCscene();

    /**
     * 重新载入综合场景角色信息
     */
    public abstract void reloadCsceneRoles();

    public abstract boolean saveCsceneModelObj(TCsceneModel csceneModel);
    /**
     * 保存综合场景数据
     * @return 保存是否成功
     */

    protected abstract boolean saveCscene();

    public abstract void saveAndCheckRole(String[] removingTargetIds,
                                          List<TCsceneRoleModel> addingTargets,
                                          List<TCsceneRoleModel> updatingTargets);
    /**
     * 保存综合场景角色信息
     */
    public abstract void saveCsceneRoles();


    protected void setCsceneId(String csceneId) {
        this.csceneId = csceneId;
    }

    public void initBase() {
        this.initMapper();
        this.initClass();
    }

    protected abstract void initMapper();
    protected abstract void initClass();

    public void initContext(CsceneContextBO context) {
        this.context = context;
        if (context != null) {
            context.loadTransparentContext();
        }
    }

    public void initLazy(String csceneId, CsceneContextBO context) {
        initBase();
        initContext(context);
        setCsceneId(csceneId);
        this.initLazy = true;
    }


    /**
     * 重新载入综合场景业务数据
     */
    public void reload() {
        if (!reloadCscene()) {
            return;
        }
        reloadCsceneRoles();
    }

    /**
     * 保存综合场景业务数据
     */
    public void save() {
        saveCscene();
        try {
            saveCsceneRoles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取底层场景id
     * @param csceneObj 综合场景
     * @return 底层场景id
     */
    public abstract String getSceneId(TCsceneModel csceneObj);

}
