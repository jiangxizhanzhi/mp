 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name QuickCreateData.java.
 // File size 482.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import lombok.Data;
import java.util.List;

/**
 * @ClassName QuickCreateData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see SnakeHttpData
 */
@Data
public class QuickCreateData implements SnakeHttpData {
    private String quickName;
    private String deviceUuid;
    private List<String> quickCreateParams;
}
