package me.fergs.phantomvouchers.commands.framework.arguments.impl;


import me.fergs.phantomvouchers.commands.framework.arguments.IArgument;

public final class ArgumentHolder {
    public IArgument<?> IArgument;
    public boolean optional = false;

    public ArgumentHolder(IArgument<?> IArgument) {
        this.IArgument = IArgument;
    }
}
