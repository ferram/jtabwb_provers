package s4.s4tab.tp;

import jtabwb.engine.ForceBranchSuccess;
import jtabwb.engine._AbstractGoal;
import jtabwb.engine._RuleWithDetails;

/**
 * Models a proof-search success due to a global cache match.
 * 
 * @author Mauro Ferrari
 *
 */
class RuleGlobalCacheSuccess extends ForceBranchSuccess implements _RuleWithDetails {

  public RuleGlobalCacheSuccess(_AbstractGoal premise) {
    super("GC_SUCCESS", premise);
  }

  @Override
  public String getDetails() {
    return "Cache SUCCESS for\n" + premise().format();
  }

  
  
}
