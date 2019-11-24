package com.leyou.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-24 17:20
 **/
@Slf4j
public class JsonUtils {

    public static final ObjectMapper mapper = new ObjectMapper();

//    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    @Nullable
    public static String serialize(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj.getClass() == String.class) {
            return (String) obj;
        }
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("json序列化出错：" + obj, e);
            return null;
        }
    }

    @Nullable
    public static <T> T parse(String json, Class<T> tClass) {
        try {
            return mapper.readValue(json, tClass);
        } catch (IOException e) {
            log.error("json解析出错：" + json, e);
            return null;
        }
    }

    @Nullable
    public static <E> List<E> parseList(String json, Class<E> eClass) {
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, eClass));
        } catch (IOException e) {
            log.error("json解析出错：" + json, e);
            return null;
        }
    }

    @Nullable
    public static <K, V> Map<K, V> parseMap(String json, Class<K> kClass, Class<V> vClass) {
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructMapType(Map.class, kClass, vClass));
        } catch (IOException e) {
            log.error("json解析出错：" + json, e);
            return null;
        }
    }

    @Nullable
    public static <T> T nativeRead(String json, TypeReference<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            log.error("json解析出错：" + json, e);
            return null;
        }
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class User{
        String name;
        String age;
    }

    public static void main(String[] args) {
//        序列化，将对象类型转化为json格式
        User user1 = new User("jack", "18");
        String user = serialize(user1);
        System.out.println(user);
//        反序列化
//        String parse = parse(user, String.class);
//        System.out.println(parse);
        User parse1 = parse(user, User.class);
        System.out.println(parse1);

//        parseList()
        String list="[1,2,3,45,6,7]";
        List <Integer> integers = parseList(list, Integer.class);
        System.out.println(integers);

//        parseMap()
        //language=JSON
        String map="{\"name\": \"jack\",\"age\": \"21\"}";
        Map <String, String> stringStringMap = parseMap(map, String.class, String.class);
        System.out.println(stringStringMap);

//        复杂类型
        String userList="[{\"name\": \"jack\",\"age\": \"21\"},{\"name\": \"lucy\",\"age\": \"18\"}]";

        List <Map <String, String>> maps = nativeRead(userList, new TypeReference <List <Map <String, String>>>() {
        });
        System.out.println(maps);
        for(Map<String,String> map2:maps){
            System.out.println(map2);
        }


    }
}
