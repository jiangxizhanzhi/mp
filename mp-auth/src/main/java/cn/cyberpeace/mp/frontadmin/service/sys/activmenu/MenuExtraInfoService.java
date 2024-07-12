 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MenuExtraInfoService.java.
 // File size 1859.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service.sys.activmenu;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.frontadmin.model.MenuExtraInfoModel;
import cn.cyberpeace.mp.frontadmin.repository.MenuExtraInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName MenuExtraInfoService
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/12/17 10:05
 * @Version 1.0.0
 */
@Service
@Slf4j
public class MenuExtraInfoService {

    @Autowired
    private MenuExtraInfoMapper menuExtraInfoMapper;

    /**
     * save
     *
     * @param menuExtraInfoModel
     * @return boolean
     */
    public boolean save(MenuExtraInfoModel menuExtraInfoModel) {
        menuExtraInfoMapper.create(menuExtraInfoModel);
        return true;
    }

    /**
     * bulkCreate
     *
     * @param menuExtraInfoModels
     * @return boolean
     */
    public boolean bulkCreate(List<MenuExtraInfoModel> menuExtraInfoModels) {
        int result = menuExtraInfoMapper.bulkCreate(menuExtraInfoModels).getSuccessCount();
        log.info("added {} menu extra info rows", result);
        return true;
    }

    /**
     * listAll
     *
     * @return
     */
    public List<MenuExtraInfoModel> listAll() {
        return menuExtraInfoMapper.list(new HumanCondition());
    }

    /**
     * deleteAll
     *
     * @return boolean
     */
    public boolean deleteAll() {
        HumanCondition condition = new HumanCondition()
                .filterGreaterThan(MenuExtraInfoModel::getPk, MenuExtraInfoModel.PK_DELETE_ALL_START);
        int result = menuExtraInfoMapper.filterDelete(condition);
        return true;
    }

}
