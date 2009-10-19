package aima.core.search.framework;

import java.util.Comparator;
import java.util.List;

import aima.core.agent.Action;
import aima.core.search.nodestore.PriorityNodeStore;

/**
 * @author Ravi Mohan
 * 
 */
public abstract class PrioritySearch implements Search {
	protected QueueSearch search;

	public List<Action> search(Problem p) throws Exception {
		return search.search(p, new PriorityNodeStore(getComparator(p)));
	}

	public Metrics getMetrics() {
		return search.getMetrics();
	}

	//
	// PROTECTED METHODS
	//
	protected abstract Comparator<Node> getComparator(Problem p);
}