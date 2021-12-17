package cn.cyberpeace.mp.front.admin.service.sys.activmenu;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.front.admin.model.ActivEditionMenuModel;
import cn.cyberpeace.mp.front.admin.model.ActivKeyMenuModel;
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
public class MenuRebuildService {


    @Autowired
    private TargetService targetService;

    @Autowired
    private ActivKeyMenuService keyMenuService;

    @Autowired
    private ActivEditionMenuService editionMenuService;


    /**
     * 该方法会清除所有菜单(target)，激活项菜单关联关系(activKeyMenu)，激活版本菜单关联关系(activEditionMenu)
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean cleanAllTargetsAndKeyOrEditionRelations() {
        boolean re1 = targetService.deleteAll();
        boolean re2 = keyMenuService.deleteAll();
        boolean re3 = editionMenuService.deleteAll();
        if (re1 && re2 && re3) {
            return true;
        }
        log.error("clean all targets and activ key/edition relations failed!");
        throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
    }


    /**
     * 该方法会重新插入所有菜单(target)，激活项菜单关联关系(activKeyMenu)，激活版本菜单关联关系(activEditionMenu)
     *
     * @param targets
     * @param keyMenus
     * @param editionMenus
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean rebuildMenusAndKeyOrEditionRelations(List<TargetModel> targets, List<ActivKeyMenuModel> keyMenus, List<ActivEditionMenuModel> editionMenus) {
        ServiceResult<Integer> result = targetService.bulkCreate(targets);
        boolean ret1 = keyMenuService.bulkCreate(keyMenus);
        boolean ret2 = editionMenuService.bulkCreate(editionMenus);

        if (Objects.nonNull(result.getSource()) && result.getSource() > 0 && ret1 && ret2) {
            return true;
        }
        log.error("rebuild all targets and activ key/edition relations failed!");
        throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
    }

}
