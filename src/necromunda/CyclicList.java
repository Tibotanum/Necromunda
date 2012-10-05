package necromunda;

import java.util.*;

public class CyclicList<E> extends ArrayList<E> {
	private int index;
	
	public E current() {
		return get(index);
	}
	
	public E next() {
		if (index < size() - 1) {
			index++;
		}
		else {
			index = 0;
		}
		
		return get(index);
	}

	@Override
	public E remove(int index) {
		if (index > 0) {
			this.index = index - 1;
		}
		return super.remove(index);
	}

	@Override
	public boolean remove(Object o) {
		int index = indexOf(o);
		
		if (index > 0) {
			this.index = index - 1;
		}
		
		return super.remove(o);
	}

	@Override
	public void clear() {
		index = 0;
		super.clear();
	}

	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		if (fromIndex > 0) {
			this.index = fromIndex - 1;
		}
		super.removeRange(fromIndex, toIndex);
	}
}
