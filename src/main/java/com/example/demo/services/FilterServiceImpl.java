package com.example.demo.services;

import com.example.demo.model.filter.FilterContext;
import com.example.demo.services.filters.SimpleExcludeStatusFilter;
import com.example.demo.services.filters.Filter;

import java.util.HashSet;
import java.util.Set;


public class FilterServiceImpl {

    private Set<Filter> filterChain = new HashSet<>();

    {
        // Заполняем цепочку фильтрами
        filterChain.add(new SimpleExcludeStatusFilter());
    }

    public FilterContext filter(FilterContext context) {
        for (Filter filter : filterChain) {
            filter.filter(context);
        }
        return context;
    }

}
