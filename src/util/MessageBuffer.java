package util;

public class MessageBuffer<E extends String> extends LimitedLinkedList<E> {

    public MessageBuffer(int limit) {
        super(limit);
    }

    @Override
    public boolean add(E e) {
        boolean added = false;
        
        String string = e.trim();
        
        if (!string.isEmpty()) {
            added = super.add(e);
        }
        
        return added; 
    }

}
