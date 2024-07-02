package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.target.TargetBo;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.TargetOperation;
import cn.cyberpeace.mp.auth.service.TargetService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Component
@Scope("prototype")
public class QueryChildTargetOperation implements TargetOperation<TargetBo> {

    @Autowired
    @Qualifier("targetServiceImpl")
    private TargetService targetService;


    /**
     * doExecute
     *
     * @param targetBo
     * @return
     */
    @Override
    public TargetBo doExecute(TargetBo targetBo) {
//        if (StringUtils.isBlank(targetDto.getParent())) {
//            return targetDto;
//        }

        List<TargetModel> list = targetService.listByParent(targetBo.getId());
        ArrayList childList = new ArrayList();
        for (TargetModel target : list) {
            TargetBo childDto = new TargetBo();
            BeanUtils.copyProperties(target, childDto);
            childDto.execute(this);
            childList.add(childDto);
        }
        targetBo.setChildren(childList);

        return targetBo;
    }
}
