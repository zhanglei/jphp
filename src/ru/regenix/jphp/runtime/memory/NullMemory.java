package ru.regenix.jphp.runtime.memory;

import ru.regenix.jphp.runtime.memory.support.Memory;

public class NullMemory extends FalseMemory {

    public final static NullMemory INSTANCE = new NullMemory();

    protected NullMemory() {
        super(Type.NULL);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return true;
    }

    @Override
    public boolean identical(Memory memory) {
        return memory.type == Type.NULL;
    }

    @Override
    public boolean identical(boolean value) {
        return false;
    }
}
