package cn.cyberpeace.mp.frontadmin.service.sys.activmenu;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.frontadmin.model.MenuExtraInfoModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;


@Service
@Slf4j
public class MenuReImportSupportService {

    @Autowired
    @Qualifier("targetServiceImpl")
    private TargetService targetService;

    @Autowired
    private MenuExtraInfoService menuExtraInfoService;


    
    @Transactional(rollbackFor = Exception.class)
    public boolean rebuildAllMenusAndExtraInfos(List<TargetModel> targets, List<MenuExtraInfoModel> menuExtraInfoModels) {
        
        boolean cleanSuccess = cleanAllMenusAndExtraInfos();
        boolean reImportSuccess = false;
        if (cleanSuccess) {
            
            reImportSuccess = reInsertAllMenusAndExtraInfos(targets, menuExtraInfoModels);
        }
        return reImportSuccess;
    }


    
    @Transactional(rollbackFor = Exception.class)
    public boolean rebuildAllMenus(List<TargetModel> targets) {
        
        boolean cleanSuccess = cleanAllMenus();
        boolean reImportSuccess = false;
        if (cleanSuccess) {
            
            reImportSuccess = reInsertAllMenus(targets);
        }
        return reImportSuccess;
    }

    
    private boolean cleanAllMenus() {
        boolean re1 = targetService.deleteAll();
        if (re1) {
            return true;
        }
        log.error("clean all menus failed!");
        throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
    }

    
    private boolean reInsertAllMenus(List<TargetModel> targets) {
        ServiceResult<Integer> result = targetService.bulkCreate(targets);
        if (Objects.nonNull(result.getSource()) && result.getSource() > 0) {
            return true;
        }
        log.error("rebuild all menus failed!");
        throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
    }

    
    private boolean cleanAllMenusAndExtraInfos() {
        boolean re1 = targetService.deleteAll();
        boolean re2 = menuExtraInfoService.deleteAll();
        if (re1 && re2) {
            return true;
        }
        log.error("clean all menus and menus extra infos failed!");
        throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
    }


    
    private boolean reInsertAllMenusAndExtraInfos(List<TargetModel> targets, List<MenuExtraInfoModel> menuExtraInfoModels) {
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
