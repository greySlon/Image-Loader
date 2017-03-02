package com.abinail.interfaces;

/**
 * Created by Sergii on 14.02.2017.
 */
public interface Listener<T> {
    void eventHandler(Object sender, T args);
}
