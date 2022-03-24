package xyz.ollieee.model.finder;

import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

class StrategyRegistry {
    
    private final Map<Class<? extends PathfinderStrategy>, PathfinderStrategy> strategyCache = new HashMap<>();
    
    public PathfinderStrategy attemptRegister(Class<? extends PathfinderStrategy> strategyType) {
        
        if (strategyCache.containsKey(strategyType))
            return strategyCache.get(strategyType);
        
        PathfinderStrategy pathfinderStrategy = null;
        try {
            pathfinderStrategy = strategyType.getDeclaredConstructor().newInstance();
            strategyCache.put(strategyType, pathfinderStrategy);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        
        return pathfinderStrategy;
    }
    
    public Map<Class<? extends PathfinderStrategy>, PathfinderStrategy> getStrategyCache() {
        return strategyCache;
    }
}
