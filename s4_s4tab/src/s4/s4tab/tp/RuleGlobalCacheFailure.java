package s4.s4tab.tp;

import jtabwb.engine.ForceBranchFailure;
import jtabwb.engine._AbstractGoal;
import jtabwb.engine._RuleWithDetails;

/**
 * Models a proof-search failure due to a global cache match.
 * 
 * @author Mauro Ferrari
 *
 */
class RuleGlobalCacheFailure extends ForceBranchFailure implements _RuleWithDetails {

  public RuleGlobalCacheFailure(_AbstractGoal premise) {
    super("GC_FAILURE", premise);
  }

  @Override
  public String getDetails() {
    return "Cache FAILURE for\n" + goal().format();
  }

}
