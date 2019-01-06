package com.zerra.common.registry;

import java.util.List;

public interface IRegistry<T> {

    List<IEntryHolder<T>> getEntryHolders();

    List<T> getEntries();

}
