package cpl.g3c.calculus;

import jtabwb.engine.ProofSearchResult;
import jtabwb.engine._AbstractGoal;
import jtabwb.engine._ClashDetectionRule;
import jtabwbx.prop.formula._Sequent;

/**
 * The {@link #status()} method returns SUCCESS iff goal is an axiom sequent of
 * G3c.
 * 
 * @author Mauro Ferrari
 *
 */
public class ClashDetectionRule implements _ClashDetectionRule {

  private _Sequent premise;

  public ClashDetectionRule(_Sequent premise) {
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
    return premise.isIdentityAxiom() ? ProofSearchResult.SUCCESS : ProofSearchResult.FAILURE;
  }
}
