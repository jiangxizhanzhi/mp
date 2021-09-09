package cn.cyberpeace.mp.devops.cluster.converter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwitchConvertModel {

    private String label;

    private String value;

    private List<Children> children;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Children{

        private String label;

        private String value;
    }
}
