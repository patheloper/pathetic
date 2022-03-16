package xyz.ollieee.api.event;

import lombok.NonNull;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PathingEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @NonNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

}
