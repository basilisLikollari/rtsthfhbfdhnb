package aima.logic.fol.inference;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import aima.logic.fol.kb.DefiniteClauseKnowledgeBase;
import aima.logic.fol.kb.FOLKnowledgeBase;
import aima.logic.fol.kb.data.DefiniteClause;
import aima.logic.fol.parsing.ast.Predicate;
import aima.logic.fol.parsing.ast.Sentence;
import aima.logic.fol.parsing.ast.Term;
import aima.logic.fol.parsing.ast.Variable;

/**
 * Artificial Intelligence A Modern Approach (2nd Edition): Figure 9.3, page 282.
 * 
 * <code>
 * function FOL-FC-ASK(KB, alpha) returns a substitution or false
 *   inputs: KB, the knowledge base, a set of first order definite clauses
 *           alpha, the query, an atomic sentence
 *   local variables: new, the new sentences inferred on each iteration
 *   
 *   repeat until new is empty
 *      new <- {}
 *      for each sentence r in KB do
 *          (p1 ^ ... ^ pn => q) <- STANDARDIZE-APART(r)
 *          for each theta such that SUBST(theta, p1 ^ ... ^ pn) = SUBST(theta, p'1 ^ ... ^ p'n)
 *                         for some p'1,...,p'n in KB
 *              q' <- SUBST(theta, q)
 *              if q' is not a renaming of some sentence already in KB or new then do
 *                   add q' to new
 *                   theta <- UNIFY(q', alpha)
 *                   if theta is not fail then return theta
 *      add new to KB
 *   return false
 * </code>
 * 
 * Figure 9.3 A conceptually straightforward, but very inefficient forward-chaining algo-
 * rithm. On each iteration, it adds to KB all the atomic sentences that can be inferred in one
 * step from the implication sentences and the atomic sentences already in KB.
 */

/**
 * @author Ciaran O'Reilly
 * 
 */
public class FOLFowardChainAsk implements InferenceProcedure {
	
	public FOLFowardChainAsk() {
	}

	//
	// START-InferenceProcedure
	
	/**
	 * <code>
	 * function FOL-FC-ASK(KB, alpha) returns a substitution or false
	 *   inputs: KB, the knowledge base, a set of first order definite clauses
	 *           alpha, the query, an atomic sentence
	 * </code>
	 */
	public List<Map<Variable, Term>> ask(FOLKnowledgeBase kb, Sentence query) {
		// Assertions on the type of KB and queries this Inference procedure
		// supports
		if (!DefiniteClauseKnowledgeBase.class.isInstance(kb)) {
			throw new IllegalArgumentException(
					"Can only perform FOL-FC-ASK inference on a Definite Clause KB.");
		}
		if (!Predicate.class.isInstance(query)) {
			throw new IllegalArgumentException(
					"Only Predicate Queries are supported.");
		}

		DefiniteClauseKnowledgeBase KB = (DefiniteClauseKnowledgeBase) kb;
		Predicate alpha = (Predicate) query;
		
		// local variables: new, the new sentences inferred on each iteration
		List<Predicate> newSentences = new ArrayList<Predicate>();
		
		// Ensure query is not already a know fact before
		// attempting forward chaining.
		List<Map<Variable, Term>> answer = KB.fetch(alpha);
		if (answer.size() > 0) {
			return answer;
		}
		
		// repeat until new is empty
		do {
			
			// new <- {}
			newSentences.clear();
			// for each sentence r in KB do
			// (p1 ^ ... ^ pn => q) <-STANDARDIZE-APART(r)
			for (DefiniteClause impl : KB.getStandardizedApartImplications()) {
				// for each theta such that SUBST(theta, p1 ^ ... ^ pn) =
				// SUBST(theta, p'1 ^ ... ^ p'n)
				// --- for some p'1,...,p'n in KB
				for (Map<Variable, Term> theta : retrievePossibleSubstitutions(
						kb, impl)) {
					// q' <- SUBST(theta, q)
					Predicate qDelta = (Predicate) KB.subst(theta, impl
							.getConclusion());
					// if q' is not a renaming of some sentence already in KB or
					// new then do
					if (!KB.isRenaming(qDelta)
							&& !KB.isRenaming(qDelta, newSentences)) {
						// add q' to new
						newSentences.add(qDelta);
						// theta <- UNIFY(q', alpha)
						theta = KB.unify(qDelta, alpha);						
						// if theta is not fail then return theta
						if (null != theta) {
							kb.tell(newSentences);
							return KB.fetch(alpha);
						}
					}
				}
			}
			// add new to KB
			KB.tell(newSentences);
		} while (newSentences.size() > 0);
		
		// return false
		return new ArrayList<Map<Variable, Term>>();
	}
	
	// END-InferenceProcedure
	//

	// 
	// PRIVATE METHODS
	//

	// TODO: Consider moving this logic somewhere else

	private List<Map<Variable, Term>> retrievePossibleSubstitutions(
			FOLKnowledgeBase kb,
			DefiniteClause dc) {
		List<Map<Variable, Term>> possibleSubstitutions = new ArrayList<Map<Variable, Term>>();

		List<Predicate> premises = dc.getPremises();

		if (premises.size() > 0) {
			Predicate first = premises.get(0);
			List<Predicate> rest = new ArrayList<Predicate>(premises);
			rest.remove(0);

			retrievePossibleSubstitutions(kb,
					new LinkedHashMap<Variable, Term>(), first, rest,
					possibleSubstitutions);
		}

		return possibleSubstitutions;
	}
	
	private void retrievePossibleSubstitutions(FOLKnowledgeBase kb,
			Map<Variable, Term> theta, Predicate p,
			List<Predicate> remainingPredicates,
			List<Map<Variable, Term>> possibleSubstitutions) {
		
		List<Map<Variable, Term>> pSubsts = kb.fetch((Predicate) kb.subst(
				theta, p));
		if (null == pSubsts) {
			return;
		}
		
		for (Map<Variable, Term> psubst : pSubsts) {
			psubst.putAll(theta);
			if (remainingPredicates.size() == 0) {
				possibleSubstitutions.add(psubst);
			} else {
				Predicate first = remainingPredicates.get(0);
				List<Predicate> rest = new ArrayList<Predicate>(
						remainingPredicates);
				rest.remove(0);
				retrievePossibleSubstitutions(kb, psubst, first, rest,
						possibleSubstitutions);
			}
		}
	}
}
