package com.example.demo.model.filter;

import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.status.PowerNodeStatusMeta;
import com.example.demo.model.status.Status;
import com.example.demo.model.status.StatusMeta;
import lombok.Builder;

import java.util.Iterator;
import java.util.Set;

@Builder
public record FilterContext(
    Set<PowerNodeStatusMeta> possibleStatuses,
    Set<Status> mustStatuses,
    PowerNode node) {

    public void clearPossibleStatuses(){
        Iterator<PowerNodeStatusMeta> iterator = possibleStatuses.iterator();
        while (iterator.hasNext()) {
            PowerNodeStatusMeta next = iterator.next();
            if (next.voltageLevels().isEmpty()) {
                iterator.remove();
            }
        }
    }
}
