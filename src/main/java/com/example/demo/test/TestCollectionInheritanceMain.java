package com.example.demo.test;

import java.util.ArrayList;
import java.util.List;

public class TestCollectionInheritanceMain {

    static class A {
        protected int a = 1;
    }

    static class B extends A {
        protected int b = 2;
    }

    public static void main(String[] args) {
        List<A> testList = new ArrayList<>();
        testList.add(new A());
        testList.add(new B());
    }

}
