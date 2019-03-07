package ipl.frj.gbu.rules;

import ipl.frj.gbu.sequent.GbuRegularSequent;
import ipl.frj.gbu.sequent._GbuSequent;
import jtabwb.engine.ProofSearchResult;
import jtabwb.engine._ClashDetectionRule;

public class Reg_ClashDetectionRule implements _ClashDetectionRule, _GbuRule {

  public Reg_ClashDetectionRule(GbuRegularSequent goal) {
    super();
    this.goal = goal;
  }

  private GbuRegularSequent goal;

  @Override
  public String name() {
    return GbuRuleIdentifiers.REG_CLASH_DETECTION.name();
  }

  @Override
  public GbuRuleIdentifiers getRuleIdentifier() {
    return GbuRuleIdentifiers.REG_CLASH_DETECTION;
  }

  @Override
  public _GbuSequent goal() {
    return goal;
  }

  @Override
  public ProofSearchResult status() {
    if (goal.isIdentityAxiom() || goal.isFalseInLeftSide())
      return ProofSearchResult.SUCCESS;
    else
      return ProofSearchResult.FAILURE;

  }

}
