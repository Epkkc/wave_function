package com.example.demo;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RuleService {

    private final NodeMatrix matrix;

    public RuleService(NodeMatrix matrix) {
        this.matrix = matrix;
    }

    public Set<SurfaceType> getAreaSurface(NodeMeta meta) {
        return matrix.getArea(meta).stream().map(o->o.map(NodeMeta::getSurface).orElse(null)).filter(Objects::nonNull).collect(Collectors.toSet());
    }


    public List<NodeMeta> getAreaTilesWithSurface(NodeMeta meta, SurfaceType type) {
        return matrix.getArea(meta).stream().filter(o -> o.filter(m -> type.equals(m.getSurface())).isPresent()).map(Optional::get).collect(Collectors.toList());
    }

    public boolean isShoreValid(NodeMeta meta) {
        return getAreaSurface(meta).containsAll(List.of(SurfaceType.LAND, SurfaceType.SEA));
    }

    public SurfaceType getShoreMissingTile(NodeMeta meta) {
        return getAreaSurface(meta).stream().anyMatch(Predicate.isEqual(SurfaceType.LAND)) ? SurfaceType.SEA : SurfaceType.LAND;
//        Set<SurfaceType> areaSurface = getAreaSurface(meta);
//        if (areaSurface.contains(SurfaceType.LAND)) {
//            return SurfaceType.SEA;
//        } else {
//            return SurfaceType.LAND;
//        }
    }
}
