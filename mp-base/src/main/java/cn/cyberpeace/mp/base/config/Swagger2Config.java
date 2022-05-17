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
        // 为枚举字段设置注释
        descForEnumFields(context);
    }

    /**
     * 为枚举字段设置注释
     */
    private void descForEnumFields(ModelPropertyContext context) {
        Optional<ApiModelProperty> annotation = Optional.empty();

        // 找到 @ApiModelProperty 注解修饰的枚举类
        if (context.getAnnotatedElement().isPresent()) {
            annotation = ApiModelProperties.findApiModePropertyAnnotation(context.getAnnotatedElement().get());
        }
        if (context.getBeanPropertyDefinition().isPresent()) {
            annotation = Annotations.findPropertyAnnotation(context.getBeanPropertyDefinition().get(),
                    ApiModelProperty.class);
        }

        // 没有@ApiModelProperty 或者 notes 属性没有值，直接返回
        if (!annotation.isPresent() || StringUtils.isEmpty((annotation.get()).notes())) {
            return;
        }

        // @ApiModelProperties中的notes指定的class类型
        Class<?> rawPrimaryType;
        try {
            rawPrimaryType = Class.forName((annotation.get()).notes());
        } catch (ClassNotFoundException e) {
            // 如果指定的类型无法转化，直接忽略
            return;
        }

        Object[] subItemRecords = null;
        var swaggerDisplayEnum = AnnotationUtils.findAnnotation(rawPrimaryType, SwaggerDisplayEnum.class);
        // 判断是否存在 @SwaggerDisplayEnum 注解，并且 rawPrimaryType 是枚举
        if (null != swaggerDisplayEnum && Enum.class.isAssignableFrom(rawPrimaryType)) {
            // 拿到枚举的所有的值
            subItemRecords = rawPrimaryType.getEnumConstants();
        }
        if (null == subItemRecords) {
            return;
        }

        final List<String> displayValues = Arrays.stream(subItemRecords)
                .filter(Objects::nonNull)
                // 调用枚举类的 toString 方法
                .map(Object::toString)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        String joinText = " (" + String.join("; ", displayValues) + ")";
        try {
            // 拿到字段上原先的描述
            Field mField = ModelPropertyBuilder.class.getDeclaredField("description");
            mField.setAccessible(true);
            // context 中的 builder 对象保存了字段的信息
            joinText = annotation.get().value() + joinText;
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        // 设置新的字段说明并且设置字段类型
        context.getSpecificationBuilder().description(joinText).build();
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }

}
