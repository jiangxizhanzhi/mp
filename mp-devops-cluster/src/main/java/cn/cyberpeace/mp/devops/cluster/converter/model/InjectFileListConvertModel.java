package cn.cyberpeace.mp.devops.cluster.converter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文件注入
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InjectFileListConvertModel extends BasePageConvertModel {

    private List<InjectFileConvertModel> modelList;
}
