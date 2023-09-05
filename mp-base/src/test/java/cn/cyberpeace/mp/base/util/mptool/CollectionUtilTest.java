package cn.cyberpeace.mp.base.util.mptool;

import org.junit.jupiter.api.Test;

import java.util.List;

class CollectionUtilTest {

    @Test
    void toList() {
        List<String> stringList1 = CollectionUtil.toList();
        System.out.println(stringList1);

        List<String> stringList2 = CollectionUtil.toList("Hello", "World");
        System.out.println(stringList2);
    }

    @Test
    void isNotEmpty() {
    }

    @Test
    void isEmpty() {
    }

    @Test
    void testIsNotEmpty() {
    }

    @Test
    void reverse() {
    }
}