 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseSingleton.java.
 // File size 708.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.constant;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName BaseSingleton
 * @Description 模块下单例对象的快捷引用
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Component
public class BaseSingleton {

    // ✅ 只读访问方法
    /** 模块配置bean的引用, 需要在bean初始化完成之后调用 TODO: 适配配置热更新场景 */
    @Getter
    private static BaseAppConfig baseAppConfig;



    @Autowired
    public BaseSingleton(BaseAppConfig baseAppConfig) {
        // 只初始化一次
        if (BaseSingleton.baseAppConfig == null) {
            BaseSingleton.baseAppConfig = baseAppConfig;
        }
    }

}
