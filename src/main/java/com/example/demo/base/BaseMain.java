package com.example.demo.base;

import com.example.demo.base.model.configuration.GeneralResult;
import com.example.demo.base.service.BaseAlgorithmService;
import com.example.demo.base.service.ConfigurationStaticSupplier;

public class BaseMain {

    public static void main(String[] args) {

        int failedCount = 0;
        boolean totalValid = true;
        for (int i = 0; i < ConfigurationStaticSupplier.baseAlgorithmIterations; i++) {
            BaseAlgorithmService baseAlgorithmService = new BaseAlgorithmService(ConfigurationStaticSupplier.rows, ConfigurationStaticSupplier.columns, ConfigurationStaticSupplier.numberOfNodes, ConfigurationStaticSupplier.numberOfEdges);
            GeneralResult result = baseAlgorithmService.startAlgo();
            boolean valid = result.getErrorMessage().isEmpty();
            totalValid &= valid;
            System.out.printf("Iter = %d, valid = %s%n", i, valid);
            if (!valid) {
                System.out.println("Error message :\n" + result.getErrorMessage());
                failedCount++;
            }
        }

        System.out.println("Total valid = " + totalValid);
        System.out.println("Failed count = " + failedCount);

    }

}
