package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.excel.ImportUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Repository
@Slf4j
public class UploadDAO {

    public void save(List<ImportUser> list) {
        // 如果是mybatis,尽量别直接调用多次insert,自己写一个mapper里面新增一个方法batchInsert,所有数据一次性插入
        log.info("{}", list);
    }

}
