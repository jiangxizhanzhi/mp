package cn.cyberpeace.mp.devopscluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeTreeItemModel {

    
    private Integer l1Id;

    
    private String l1Name;

    
    private List<L1Child> l1Child;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static final class L1Child implements Serializable {

        
        private Integer l2Id;

        
        private String l2Name;

        
        private Integer l1ParentId;

        
        private List<L2Child> l2Child;

        @NoArgsConstructor
        @AllArgsConstructor
        @Data
        public static final class L2Child{

            
            private Integer l3Id;

            
            private String l3Name;

            
            private Integer l2ParentId;

            
            private Integer l1ParentId;
        }
    }
}
