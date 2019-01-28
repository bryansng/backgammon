import java.util.Iterator;

public class Stack<E> implements StackInterface<E>, Iterable<E> {
	private final int MAXSIZE = 15;
	private int size = 0;
	private Node<E> top;
	
	public Stack() {
		top = null;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public E top() {
		if (isEmpty()) {return null;}
		return top.getData();
	}

	@Override
	public void push(E e) throws PointOverflowException {
		if (size <= MAXSIZE) {
			top = new Node<E>(e, top);
			size++;
		}
		else {
			throw new PointOverflowException("MAXSIZE: " + MAXSIZE + ", but current size: " + size);
		}
	}

	@Override
	public E pop() {
		if (isEmpty()) {return null;}
		E temp = top.getData();
		top = top.getNext();
		size--;
		return temp;
	}

	@Override
	public Iterator<E> iterator() {
		return new listIterator<E>();
	}
	
	private class listIterator<T> implements Iterator<T> {
		@SuppressWarnings("unchecked")
		Node<T> curr = (Node<T>) top;
		int pos = 0;

		@Override
		public boolean hasNext() {
			return pos != size;
		}

		@Override
		public T next() {
			T temp = curr.getData();
			curr = curr.getNext();
			pos++;
			return temp;
		}
	}
}
