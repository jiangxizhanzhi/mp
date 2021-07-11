package cn.cyberpeace.mp.base.config;

import cn.cyberpeace.mp.base.annotation.SwaggerDisplayEnum;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ModelPropertyBuilder;
import springfox.documentation.schema.Annotations;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.swagger.schema.ApiModelProperties;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableKnife4j
@EnableSwagger2
public class Swagger2Config implements ModelPropertyBuilderPlugin {

    @Autowired
    public BaseAppConfig baseAppConfig;

    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(baseAppConfig.getSwaggerDocTitle())
                .description(baseAppConfig.getSwaggerDocDescription())
                .termsOfServiceUrl(baseAppConfig.getSwaggerDocUrl())
                .version(baseAppConfig.getSwaggerDocVersion())
                .build();
    }

    @Override
    public void apply(ModelPropertyContext context) {
        
        descForEnumFields(context);
    }

    
    private void descForEnumFields(ModelPropertyContext context) {
        Optional<ApiModelProperty> annotation = Optional.empty();

        
        if (context.getAnnotatedElement().isPresent()) {
            annotation = ApiModelProperties.findApiModePropertyAnnotation(context.getAnnotatedElement().get());
        }
        if (context.getBeanPropertyDefinition().isPresent()) {
            annotation = Annotations.findPropertyAnnotation(context.getBeanPropertyDefinition().get(),
                    ApiModelProperty.class);
        }

        
        if (!annotation.isPresent() || StringUtils.isEmpty((annotation.get()).notes())) {
            return;
        }

        
        Class<?> rawPrimaryType;
        try {
            rawPrimaryType = Class.forName((annotation.get()).notes());
        } catch (ClassNotFoundException e) {
            
            return;
        }

        Object[] subItemRecords = null;
        var swaggerDisplayEnum = AnnotationUtils.findAnnotation(rawPrimaryType, SwaggerDisplayEnum.class);
        
        if (null != swaggerDisplayEnum && Enum.class.isAssignableFrom(rawPrimaryType)) {
            
            subItemRecords = rawPrimaryType.getEnumConstants();
        }
        if (null == subItemRecords) {
            return;
        }

        final List<String> displayValues = Arrays.stream(subItemRecords)
                .filter(Objects::nonNull)
                
                .map(Object::toString)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        String joinText = " (" + String.join("; ", displayValues) + ")";
        try {
            
            Field mField = ModelPropertyBuilder.class.getDeclaredField("description");
            mField.setAccessible(true);
            
            joinText = annotation.get().value() + joinText;
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        
        context.getSpecificationBuilder().description(joinText).build();
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }

}
