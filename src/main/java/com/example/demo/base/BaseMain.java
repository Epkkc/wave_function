package com.example.demo.base;

import com.example.demo.base.model.configuration.GeneralResult;
import com.example.demo.base.service.BaseAlgorithmService;

public class BaseMain {

    static int rows = 50;
    static int columns = 50;
    static int numberOfNodes = 40;
    static int numberOfEdges = 39;

    public static void main(String[] args) {

        int iters = 100;
        boolean totalValid = true;
        for (int i = 0; i < iters; i++) {
            BaseAlgorithmService baseAlgorithmService = new BaseAlgorithmService(rows, columns, numberOfNodes, numberOfEdges);
            GeneralResult result = baseAlgorithmService.startAlgo();
            boolean valid = result.getErrorMessage().isEmpty();
            totalValid &= valid;
            System.out.printf("Iter = %d, valid = %s%n", i, valid);
            if (!valid) {
                System.out.println("Error message :\n" + result.getErrorMessage());
            }
        }

        System.out.println("Total valid = " + totalValid);

    }

}
