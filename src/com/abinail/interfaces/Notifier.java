package com.abinail.interfaces;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergii on 14.02.2017.
 */
public class Notifier<T> {
    private List<Listener<T>> listnersList = new ArrayList<>();

    public void raiseEvent(Object sender, T arg) {
        for (Listener<T> listner : listnersList) {
            listner.eventHandler(sender, arg);
        }
    }

    public Event<T> getEvent() {
        return new Event<T>() {
            @Override
            public void addEventListner(Listener<T> listner) {
                if (listner != null)
                    listnersList.add(listner);
            }

            @Override
            public void removeListner(Listener<T> listner) {
                if (listner != null)
                    listnersList.remove(listner);
            }
        };
    }
}
