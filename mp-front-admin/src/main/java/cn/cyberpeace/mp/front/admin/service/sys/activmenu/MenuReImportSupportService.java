package cn.cyberpeace.mp.front.admin.service.sys.activmenu;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.front.admin.model.MenuExtraInfoModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/17 11:19
 */
@Service
@Slf4j
public class MenuReImportSupportService {

    @Autowired
    private TargetService targetService;

    @Autowired
    private MenuExtraInfoService menuExtraInfoService;


    /**
     * 场景：重新导入menu.json
     * 清除并且重建menus, menu_extra_infos
     *
     * @param targets
     * @param menuExtraInfoModels
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean rebuildAllMenusAndExtraInfos(List<TargetModel> targets, List<MenuExtraInfoModel> menuExtraInfoModels) {
        // step1. clean menus, keymenus, editionmenus
        boolean cleanSuccess = cleanAllMenusAndExtraInfos();
        boolean reImportSuccess = false;
        if (cleanSuccess) {
            // step2. rebuild menus, menuExtraInfos
            reImportSuccess = reInsertAllMenusAndExtraInfos(targets, menuExtraInfoModels);
        }
        return reImportSuccess;
    }


    /**
     * 场景：重新激活
     * 清除并且重建menus
     *
     * @param targets
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean rebuildAllMenus(List<TargetModel> targets) {
        log.info("targets:{}", targets);
        // step1. clean menus, keymenus, editionmenus
        boolean cleanSuccess = cleanAllMenus();
        boolean reImportSuccess = false;
        if (cleanSuccess) {
            // step2. rebuild menus, menuExtraInfos
            reImportSuccess = reInsertAllMenus(targets);
        }
        return reImportSuccess;
    }

    /**
     * 该方法会清除所有菜单(target)
     *
     * @return
     */
    private boolean cleanAllMenus() {
        log.info("now clean all menus...");
        boolean re1 = targetService.deleteAll();
        if (re1) {
            log.info("clean all menus success!");
            return true;
        }
        log.error("clean all menus failed!");
        throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
    }

    /**
     * 该方法会重新插入所有菜单(target)
     *
     * @param targets
     * @return
     */
    private boolean reInsertAllMenus(List<TargetModel> targets) {
        log.info("now reimport menus infos...");
        ServiceResult<Integer> result = targetService.bulkCreate(targets);
        log.info("inserted {} target rows.", result.getSource());
        if (Objects.nonNull(result.getSource()) && result.getSource() > 0) {
            log.info("rebuild all menus success!");
            return true;
        }
        log.error("rebuild all menus failed!");
        throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
    }

    /**
     * 该方法会清除所有菜单(target)，菜单附属信息(menuExtraInfos)
     *
     * @return
     */
    private boolean cleanAllMenusAndExtraInfos() {
        log.info("now clean all menus and menu extra infos...");
        boolean re1 = targetService.deleteAll();
        boolean re2 = menuExtraInfoService.deleteAll();
        if (re1 && re2) {
            log.info("clean all menus and menus extra infos success!");
            return true;
        }
        log.error("clean all menus and menus extra infos failed!");
        throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
    }


    /**
     * 该方法会重新插入所有菜单(target)，菜单附属信息(menuExtraInfos)
     *
     * @param targets
     * @return
     */
    private boolean reInsertAllMenusAndExtraInfos(List<TargetModel> targets, List<MenuExtraInfoModel> menuExtraInfoModels) {
        log.info("now reimport menus and menu extra infos...");
        ServiceResult<Integer> result = targetService.bulkCreate(targets);
        log.info("inserted {} target rows.", result.getSource());
        boolean re1 = menuExtraInfoService.bulkCreate(menuExtraInfoModels);
        if (Objects.nonNull(result.getSource()) && result.getSource() > 0 && re1) {
            log.info("rebuild all menus and menu extra info success!");
            return true;
        }
        log.error("rebuild all menus and menu extra info failed!");
        throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
    }

}
