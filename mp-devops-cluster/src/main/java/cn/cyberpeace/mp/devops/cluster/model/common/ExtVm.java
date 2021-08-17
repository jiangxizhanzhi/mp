package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtVm {

    private String flavor;

    private Boolean init_support;

    private Boolean quick_create_support;

    private String quick_create_params;

    private String access_protocals;
}
