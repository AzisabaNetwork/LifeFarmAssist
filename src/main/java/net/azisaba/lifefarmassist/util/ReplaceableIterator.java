package net.azisaba.lifefarmassist.util;

import java.util.Iterator;

public interface ReplaceableIterator<E> extends Iterator<E> {
    void replace(E e);
}
