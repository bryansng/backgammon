package game;

import java.util.Iterator;

import constants.GameConstants;
import exceptions.StackOverflowException;
import javafx.scene.layout.VBox;

/**
 * This class represents a stack data structure, i.e. Last-In-First-Out data structure.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 */
public class Stack<E> extends VBox implements StackInterface<E>, Iterable<E> {
	private final int MAXSIZE = GameConstants.MAX_CHECKERS_PER_CHECKERS_STORER;
	private int size = 0;
	private Node<E> top;
	
	private static class Node<E> {
		private E data;
		private Node<E> next;
		
		public Node(E d, Node<E> n) {
			data = d;
			next = n;
		}
	}
	
	public Stack() {
		super();
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
		return top.data;
	}

	@Override
	public void push(E e) throws StackOverflowException {
		if (size <= MAXSIZE) {
			top = new Node<E>(e, top);
			size++;
		} else {
			throw new StackOverflowException("MAXSIZE: " + MAXSIZE + ", but current size: " + size);
		}
	}

	@Override
	public E pop() {
		if (isEmpty()) {return null;}
		E temp = top.data;
		top = top.next;
		size--;
		return temp;
	}
	
	@Override
	public void clear() {
		while (!isEmpty())
			pop();
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
			T temp = curr.data;
			curr = curr.next;
			pos++;
			return temp;
		}
	}
}
