package cn.cyberpeace.mp.cscene.controller.request;

import lombok.Data;

@Data
public class CsceneInstanceNodeExpansionDiskActionRequest extends CsceneInstanceNodeActionRequest {
    private String vmSizeType;
    private Integer expansionDisk;
    private String expansionDiskType;
}
