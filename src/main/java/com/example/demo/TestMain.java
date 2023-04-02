package com.example.demo;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Random;

public class TestMain {

    public static void main(String[] args) {
//        List<String> list1 = List.of("1", "2", "3");
//        List<String> list2 = List.of("2", "3", "4");
//        System.out.println(CollectionUtils.subtract(list1, list2));
        Random random = new Random();
        for (int i = 0; i < 50; i++) {

            System.out.println(random.nextInt(2));
        }


    }
}
