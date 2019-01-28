public class Node<E> {
	private E data;
	private Node<E> next;
	
	public Node(E d, Node<E> n) {
		data = d;
		next = n;
	}
	
	public Node<E> getNext() {
		return next;
	}
	
	public E getData() {
		return data;
	}
	
	public void setNext(Node<E> n) {
		next = n;
	}
	
	public void setData(E d) {
		data = d;
	}
}
