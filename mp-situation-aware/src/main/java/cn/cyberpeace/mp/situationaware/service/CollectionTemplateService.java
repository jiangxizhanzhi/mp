package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.situationaware.model.CollectionTemplateModel;
import cn.cyberpeace.mp.situationaware.model.dto.CollectionTemplatePageQueryRequest;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/24 17:22
 */
@Service
@Slf4j
public class CollectionTemplateService {


    /**
     * 分页查询探针列表
     *
     * @return
     */
    public PageInfo<CollectionTemplateModel> pageTemplates(CollectionTemplatePageQueryRequest pageQueryRequest) {
        log.info("pageQueryRequest:{}", pageQueryRequest);
        pageQueryRequest.ensure();

        // todo 通用探针

        return null;
    }


    /**
     * 新增采集模板
     * @return
     */
    public CollectionTemplateModel saveTemplate() {

    }


    /**
     * 新增采集模板
     * @return
     */
    public CollectionTemplateModel modifyTemplate() {

    }


    public CollectionTemplateModel getById(String id) {

    }

}
