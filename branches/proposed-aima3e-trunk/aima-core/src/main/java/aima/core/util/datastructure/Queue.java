package aima.core.util.datastructure;

import java.util.List;

/**
 * @author Ravi Mohan
 * 
 */

public interface Queue {
	void add(Object anItem);

	void add(List items);

	Object remove();

	Object get();
}