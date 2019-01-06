package com.zerra.common.registry;

public interface IEntryHolder<T> {

    /**
     * Get's called by The Registry of type T to aquire all the entries in this object.
     * @return all the entries in this {@link IEntryHolder}
     */
    T[] getEntries();

    /**
     * Used by the Registry of type T to aquire the registry's prefix for example; zerra has "zerra" + ":" + entryID
     * with "zerra" being the prefix
     * @return the prefix string
     */
    String getPrefix();

    /**
     * Used by the Regstry of type T to check if it needs to apply the prefix itsself or if it's done already
     * @return whether or not te registry should append the prefix in front of all entries in this {@link IEntryHolder}
     */
    boolean shouldPostPrefix();

    Class<T> getType();
}
