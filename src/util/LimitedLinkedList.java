package util;

import java.util.LinkedList;

public class LimitedLinkedList<E> extends LinkedList<E> {
    private int limit;
    
    public LimitedLinkedList(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(E e) {
        boolean added = super.add(e);
        
        if (size() > limit) {
            removeFirst();
        }
        
        return added;
    }

}
