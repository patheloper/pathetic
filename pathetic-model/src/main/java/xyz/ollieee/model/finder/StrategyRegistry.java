package xyz.ollieee.model.finder;

import xyz.ollieee.api.pathing.Pathfinder;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class StrategyRegistry {
    
    private final Map<Class<? extends PathfinderStrategy>, PathfinderStrategy> strategyCache = new HashMap<>();
    
    public PathfinderStrategy registerStrategy(Class<? extends PathfinderStrategy> strategyType) {
        
        if (strategyCache.containsKey(strategyType))
            return getStrategyByType(strategyType).orElseThrow(() -> new IllegalStateException("Whatever happened here?"));
        
        PathfinderStrategy pathfinderStrategy = null;
        try {
            pathfinderStrategy = strategyType.getDeclaredConstructor().newInstance();
            strategyCache.put(strategyType, pathfinderStrategy);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        
        return pathfinderStrategy;
    }
    
    public Optional<PathfinderStrategy> getStrategyByType(Class<? extends PathfinderStrategy> strategyType) {
        
        if(!strategyCache.containsKey(strategyType))
            return Optional.empty();
        
        return Optional.of(strategyCache.get(strategyType));
    }
    
    public Map<Class<? extends PathfinderStrategy>, PathfinderStrategy> getStrategyCache() {
        return strategyCache;
    }
}
