package cn.cyberpeace.mp.devops.log.dto;

import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.devops.log.enums.SiteTypeEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogOperateDTO<T> {

    private SiteTypeEnums siteTypeEnums;

    private OpModuleEnums module;

    private OpTypeEnums type;

    private OpTargetEnums target;

    private String title;

    /**
     * 描述
     */
    private String description;

    private String userName;

    private String userId;

    private String userType;

    private Locale locale;

    private T t;
}
