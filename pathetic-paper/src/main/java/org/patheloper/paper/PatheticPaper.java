package org.patheloper.paper;

import lombok.NonNull;
import org.patheloper.Pathetic;
import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.pathing.rules.PathingRuleSet;
import org.patheloper.bukkit.PatheticBukkit;
import org.patheloper.model.pathing.pathfinder.AStarPathfinder;
import org.patheloper.util.ErrorLogger;

public final class PatheticPaper extends PatheticBukkit {

  private static final PatheticPaper INSTANCE = new PatheticPaper();

  public static PatheticPaper getInstance() {
    return INSTANCE;
  }

  private PatheticPaper() {
      super();
  }

  @Override
  public @NonNull Pathfinder newPathfinder(PathingRuleSet pathingRuleSet) {
    if (Pathetic.isInitialized())
      return new AStarPathfinder(pathingRuleSet, new PaperTerrainProvider());

    throw ErrorLogger.logFatalError("Pathetic is not initialized");
  }
}
