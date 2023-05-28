package com.example.demo.base;

import com.example.demo.base.model.configuration.GeneralResult;
import com.example.demo.base.service.BaseAlgorithmService;

public class BaseMain {

    static int rows = 50;
    static int columns = 50;
    static int delays = 2000;
    static int numberOfNodes = 150;
    static int numberOfEdges = 170;

    public static void main(String[] args) {

        BaseAlgorithmService baseAlgorithmService = new BaseAlgorithmService(rows, columns, delays, numberOfNodes, numberOfEdges);

        GeneralResult result = baseAlgorithmService.startAlgo();
        System.out.println(result);
    }

}
