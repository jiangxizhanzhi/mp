package cn.cyberpeace.mp.devops.cluster.converter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnapshotListConvertModel extends BasePageConvertModel {

    /**
     * 快照列表
     */
    private List<SnapshotConvertModel> modelList;
}
