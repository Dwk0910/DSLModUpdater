package kr.kro.dslofficial.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        List<String> list = Arrays.asList("str1", "str2", "str3", "abc", "abcd", "ABC", "ABCD");
        list.stream()
                .filter(x -> x.startsWith("s"))
                .forEach(System.out::println);
    }
}
