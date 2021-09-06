package cn.cyberpeace.mp.devops.cluster.converter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件注入
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class InjectFileConvertModel extends BasePageConvertModel {

    private String injectUuid;

    private String name;

    private String destPath;

    private Integer size;

    private String status;
}
