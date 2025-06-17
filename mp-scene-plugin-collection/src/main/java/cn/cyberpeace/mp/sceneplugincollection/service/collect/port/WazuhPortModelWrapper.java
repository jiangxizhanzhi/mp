package cn.cyberpeace.mp.sceneplugincollection.service.collect.port;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WazuhPortModelWrapper {
    private List<WazuhPortModel> modifiedPrev;
    private List<WazuhPortModel> added;
    private List<WazuhPortModel> values;
    private List<WazuhPortModel> modified;
    private List<WazuhPortModel> deletedPrev;
}
