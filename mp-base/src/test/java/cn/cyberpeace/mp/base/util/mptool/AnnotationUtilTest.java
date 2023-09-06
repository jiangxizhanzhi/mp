package cn.cyberpeace.mp.base.util.mptool;

import org.junit.jupiter.api.Test;

@MyAnnotation("Hello, World!")
class AnnotationUtilTest {

    @Test
    void setValue() {
        // 获取注解
        MyAnnotation annotation = AnnotationUtilTest.class.getAnnotation(MyAnnotation.class);
        System.out.println(annotation.value());
        AnnotationUtil.setValue(annotation, "value", "New Value");
        System.out.println(annotation.value());
    }


}



