package s4.s4tab.tp;

import java.util.HashMap;

import s4.s4tab.nodeset._S4TabGoal;
import jtabwb.engine.ProofSearchResult;

/**
 * Implementation of a global cache storing proof-search results for goals.
 * 
 * @author Mauro Ferrari
 *
 */
class GlobalCache {

  public GlobalCache() {
    super();
    alreadyKnown = new HashMap<_S4TabGoal, ProofSearchResult>();
  }

  private HashMap<_S4TabGoal, ProofSearchResult> alreadyKnown;

  public void put(_S4TabGoal sequent, ProofSearchResult result) {
    alreadyKnown.put(sequent, result);
  }

  public ProofSearchResult get(_S4TabGoal sequent) {
    return alreadyKnown.get(sequent);
  }
}
