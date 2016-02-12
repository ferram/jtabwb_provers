package s4.s4tab.calculus;

import s4.s4tab.nodeset.Sign;
import s4.s4tab.nodeset.SignedFormula;
import s4.s4tab.nodeset._S4TabGoal;
import jtabwbx.modal.formula.ModalFormula;

/**
 * REGULAR RULE
 * 
 * <pre>
 *      S, F(\nec A)
 *    ------------------ 
 *       Sc, F A
 * </pre>
 */
public class Rule_F_NEC_nonInv extends TabS4RegularRule {

  public Rule_F_NEC_nonInv(_S4TabGoal premise, ModalFormula wff) {
    super(TabS4RuleIdentifiers.F_NEC, premise, new SignedFormula(Sign.F, wff), 1);
  }

  public _S4TabGoal conclusion(int i) {
    _S4TabGoal newgoal = goal.cloneStablePart(); // non invertibility
    newgoal.removeSigned(Sign.F, mainFormula.getWff());
    newgoal.addSigned(Sign.F, mainFormula.getWff().immediateSubformulas()[0]); // FA
    return newgoal;
  }

}
