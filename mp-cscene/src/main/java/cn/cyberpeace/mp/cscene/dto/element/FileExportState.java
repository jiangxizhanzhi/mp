package cn.cyberpeace.mp.cscene.dto.element;

import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 节点监控统计信息
 * @Author zhouxinghuai
 * @Date：2021/11/22 14:03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "节点状态信息")
public class FileExportState {

    @ApiModelProperty("文件变更数量")
    private List<CollectionHostEventBO> modifyFileCount;

    @ApiModelProperty("文件变更列表")
    private List<ElementHistogramInfo> modifyFileList;

}
