package cn.cyberpeace.mp.situationaware.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupModel {

    private String groupId;

    
    private List<String> macIds;

    
    private String url;
}
