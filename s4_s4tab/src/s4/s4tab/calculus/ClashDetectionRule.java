package s4.s4tab.calculus;

import s4.s4tab.nodeset._S4TabGoal;
import jtabwb.engine.ProofSearchResult;
import jtabwb.engine._AbstractGoal;
import jtabwb.engine._ClashDetectionRule;

/**
 * The {@link #status()} method returns SUCCESS iff one of the following holds:
 * <ul>
 * <li>the goal contains T(A) and F(A) for a formula A;</li>
 * <li>the goal contains Tc(A) and F(A) for a given formula A</li>
 * <li>the goal contains T(false) or F(true) or Tc(false)</li>
 * </ul>
 * 
 * @author Mauro Ferrari
 *
 */
public class ClashDetectionRule implements _ClashDetectionRule {

  private _S4TabGoal premise;

  public ClashDetectionRule(_S4TabGoal premise) {
    super();
    this.premise = premise;
  }

  @Override
  public String name() {
    return "AX";
  }

  @Override
  public _AbstractGoal goal() {
    return premise;
  }

  @Override
  public ProofSearchResult status() {
    return premise.checkForClash() ? ProofSearchResult.SUCCESS : ProofSearchResult.FAILURE;
  }
}
