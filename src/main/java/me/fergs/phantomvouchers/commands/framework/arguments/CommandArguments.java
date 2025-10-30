package me.fergs.phantomvouchers.commands.framework.arguments;

import java.util.HashMap;
import java.util.Map;

public class CommandArguments {
    private final Map<String, Object> args = new HashMap<>();

    public void put(String key, Object value) {
        args.put(key, value);
    }

    public Object get(String key) {
        return args.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAs(String key, Class<T> type) {
        return (T) args.get(key);
    }
}