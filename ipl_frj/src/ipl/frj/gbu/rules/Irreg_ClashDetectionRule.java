package ipl.frj.gbu.rules;

import ipl.frj.gbu.sequent.GbuIrregularSequent;
import ipl.frj.gbu.sequent._GbuSequent;
import jtabwb.engine.ProofSearchResult;
import jtabwb.engine._ClashDetectionRule;

public class Irreg_ClashDetectionRule implements _ClashDetectionRule, _GbuRule {

  public Irreg_ClashDetectionRule(GbuIrregularSequent goal) {
    super();
    this.goal = goal;
  }

  private GbuIrregularSequent goal;

  @Override
  public String name() {
    return GbuRuleIdentifiers.IRREG_CLASH_DETECTION.name();
  }

  @Override
  public GbuRuleIdentifiers getRuleIdentifier() {
    return GbuRuleIdentifiers.IRREG_CLASH_DETECTION;
  }

  @Override
  public _GbuSequent goal() {
    return goal;
  }

  @Override
  public ProofSearchResult status() {
    if (goal.isIdentityAxiom())
      return ProofSearchResult.SUCCESS;
    else
      return ProofSearchResult.FAILURE;

  }

}
