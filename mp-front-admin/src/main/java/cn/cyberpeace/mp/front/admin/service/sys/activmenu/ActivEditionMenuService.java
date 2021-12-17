package cn.cyberpeace.mp.front.admin.service.sys.activmenu;

import cn.cyberpeace.mp.front.admin.model.ActivEditionMenuModel;
import cn.cyberpeace.mp.front.admin.repository.ActivEditionMenuMapper;
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
public class ActivEditionMenuService {

    @Autowired
    private ActivEditionMenuMapper editionMenuMapper;

    public boolean create(ActivEditionMenuModel editionMenuModel) {
        editionMenuMapper.create(editionMenuModel);
        return true;
    }

    public boolean bulkCreate(List<ActivEditionMenuModel> editionMenuModels) {
        int result = editionMenuMapper.bulkCreate(editionMenuModels);
        log.info("inserted {} rows", result);
        return true;
    }

    public List<ActivEditionMenuModel> selectAll() {
        return editionMenuMapper.selectAll();
    }

    public boolean deleteAll() {
        int result = editionMenuMapper.deleteAll();
        log.info("delete {} rows", result);
        return true;
    }


}
