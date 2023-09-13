package cn.cyberpeace.mp.base.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

class JsonCommonTest {


    @Test
    void parseList() {
        String json = "[{\"name\":\"Alice\",\"age\":25},{\"name\":\"Bob\",\"age\":30}]";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Person> personList=objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Person.class));
            for (Person person : personList) {
                System.out.println(person.getName() + ", " + person.getAge());
            }
        } catch (Exception e) {
        }
    }
}

class Person {
    private String name;
    private int age;

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}