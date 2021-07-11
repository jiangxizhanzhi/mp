package cn.cyberpeace.mp.frontadmin.service.sys.activmenu;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.frontadmin.model.MenuExtraInfoModel;
import cn.cyberpeace.mp.frontadmin.repository.MenuExtraInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class MenuExtraInfoService {

    @Autowired
    private MenuExtraInfoMapper menuExtraInfoMapper;

    public boolean save(MenuExtraInfoModel menuExtraInfoModel) {
        menuExtraInfoMapper.create(menuExtraInfoModel);
        return true;
    }

    public boolean bulkCreate(List<MenuExtraInfoModel> menuExtraInfoModels) {
        int result = menuExtraInfoMapper.bulkCreate(menuExtraInfoModels).getSuccessCount();
        log.info("added {} menu extra info rows", result);
        return true;
    }

    public List<MenuExtraInfoModel> listAll() {
        return menuExtraInfoMapper.list(new HumanCondition());
    }

    public boolean deleteAll() {
        HumanCondition condition = new HumanCondition()
                .filterGreaterThan(MenuExtraInfoModel::getPk, MenuExtraInfoModel.PK_DELETE_ALL_START);
        int result = menuExtraInfoMapper.filterDelete(condition);
        return true;
    }

}
