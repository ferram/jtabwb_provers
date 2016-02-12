package s4.s4tab.calculus;

import s4.s4tab.nodeset.Sign;
import s4.s4tab.nodeset.SignedFormula;
import s4.s4tab.nodeset._S4TabGoal;
import jtabwb.engine.NoSuchSubgoalException;
import jtabwbx.modal.formula.ModalFormula;

/**
 * REGULAR BRANCHING RULE.
 * 
 * <pre>
 *              S, F(A \and B)
 *              -------------
 *              S, FA | S, FB
 * </pre>
 */
public class Rule_F_AND_branching extends TabS4RegularRule {

  public Rule_F_AND_branching(_S4TabGoal premise, ModalFormula wff) {
    super(TabS4RuleIdentifiers.F_AND, premise, new SignedFormula(Sign.F, wff), 2);
  }

  @Override
  public _S4TabGoal conclusion(int i) {
    ModalFormula wff = mainFormula.getWff();
    switch (i) {
    case 0: {
      _S4TabGoal firstBranch = goal.clone();
      firstBranch.removeSigned(Sign.F,wff);
      firstBranch.addSigned(Sign.F, wff.immediateSubformulas()[0]); // FA
      return firstBranch;
    }
    case 1: {
      _S4TabGoal secondBranch = goal.clone();
      secondBranch.removeSigned(Sign.F, wff);
      secondBranch.addSigned(Sign.F,wff.immediateSubformulas()[1]); // FB
      return secondBranch;
    }
    default:
      throw new NoSuchSubgoalException(i);
    }
  }
}
