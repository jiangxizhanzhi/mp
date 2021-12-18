package cn.cyberpeace.mp.front.admin.service.sys.activmenu;

import cn.cyberpeace.mp.front.admin.model.MenuExtraInfoModel;
import cn.cyberpeace.mp.front.admin.repository.MenuExtraInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/17 10:05
 */
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
        int result = menuExtraInfoMapper.bulkCreate(menuExtraInfoModels);
        log.info("inserted {} rows", result);
        return true;
    }

    public List<MenuExtraInfoModel> selectAll() {
        return menuExtraInfoMapper.selectAll();
    }

    public boolean deleteAll() {
        int result = menuExtraInfoMapper.deleteAll();
        log.info("delete {} rows", result);
        return true;
    }


}
