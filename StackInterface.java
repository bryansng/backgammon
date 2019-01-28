
public interface StackInterface<E> {
	boolean isEmpty();
	int size();
	E top();
	void push(E e);
	E pop();
}
