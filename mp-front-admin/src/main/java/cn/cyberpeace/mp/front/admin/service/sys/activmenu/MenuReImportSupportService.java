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


    @Transactional(rollbackFor = Exception.class)
    public boolean rebuildMenusAndExtraInfos(List<TargetModel> targets, List<MenuExtraInfoModel> menuExtraInfoModels) {
        // step1. clean menus, keymenus, editionmenus
        boolean cleanSuccess = cleanAllMenusAndExtraInfos();
        boolean reImportSuccess = false;
        if (cleanSuccess) {
            // step2. rebuild menus, menuExtraInfos
            reImportSuccess = reInsertMenusAndExtraInfos(targets, menuExtraInfoModels);
        }
        return reImportSuccess;
    }


    /**
     * 该方法会清除所有菜单(target)，激活项菜单关联关系(activKeyMenu)，激活版本菜单关联关系(activEditionMenu)
     *
     * @return
     */
    private boolean cleanAllMenusAndExtraInfos() {
        log.info("now clean menus, keymenus and editionmenus...");
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
     * 该方法会重新插入所有菜单(target)，激活项菜单关联关系(activKeyMenu)，激活版本菜单关联关系(activEditionMenu)
     *
     * @param targets
     * @return
     */
    private boolean reInsertMenusAndExtraInfos(List<TargetModel> targets, List<MenuExtraInfoModel> menuExtraInfoModels) {
        log.info("now reimport menus, keymenus and editionmenus...");
        ServiceResult<Integer> result = targetService.bulkCreate(targets);
        boolean re1 = menuExtraInfoService.bulkCreate(menuExtraInfoModels);
        if (Objects.nonNull(result.getSource()) && result.getSource() > 0 && re1) {
            log.info("rebuild all menus and menu extra info success!");
            return true;
        }
        log.error("rebuild all menus and menu extra info failed!");
        throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
    }

}
