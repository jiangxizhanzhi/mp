package cn.cyberpeace.mp.front.admin.service.sys.activmenu;

import cn.cyberpeace.mp.front.admin.model.ActivKeyMenuModel;
import cn.cyberpeace.mp.front.admin.repository.ActivKeyMenuMapper;
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
public class ActivKeyMenuService {

    @Autowired
    private ActivKeyMenuMapper keyMenuMapper;

    public boolean save(ActivKeyMenuModel keyMenuModel) {
        keyMenuMapper.create(keyMenuModel);
        return true;
    }

    public boolean bulkCreate(List<ActivKeyMenuModel> keyMenuModels) {
        int result = keyMenuMapper.bulkCreate(keyMenuModels);
        log.info("inserted {} rows", result);
        return true;
    }

    public List<ActivKeyMenuModel> selectAll() {
        return keyMenuMapper.selectAll();
    }

    public boolean deleteAll() {
        int result = keyMenuMapper.deleteAll();
        log.info("delete {} rows", result);
        return true;
    }


}
