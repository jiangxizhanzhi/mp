package cn.cyberpeace.mp.auth.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPattern;

import java.util.List;
import java.util.Set;


@Component
@Data
public class BlacklistModel {
    
    private boolean enabled;

    
    private Set<AccessItem> patterns;

    @Data
    @Builder
    public static class AccessItem {
        private String method;
        private PathPattern pattern;
    }
}
