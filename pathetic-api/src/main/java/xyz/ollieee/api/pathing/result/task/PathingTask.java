package xyz.ollieee.api.pathing.result.task;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import xyz.ollieee.api.pathing.result.progress.ProgressBar;
import xyz.ollieee.api.pathing.rules.PathingRuleSetBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PathingTask<V> {

    @Setter
    private CompletableFuture<V> result;
    @Getter
    private final PathingRuleSetBuilder.PathingRuleSet ruleSet;
    @Getter
    private final ProgressBar progressBar;

    public PathingTask(PathingRuleSetBuilder.PathingRuleSet ruleSet) {
        this.ruleSet = ruleSet;
        this.progressBar = new ProgressBar(ruleSet.getStart(), ruleSet.getTarget());
    }

    public boolean isFinished() {
        return this.result.isDone();
    }

    @SneakyThrows
    public V getResult() {
        return this.result.join();
    }

    public void accept(Consumer<V> callback) {
        this.result.thenAccept(callback);
    }

}
