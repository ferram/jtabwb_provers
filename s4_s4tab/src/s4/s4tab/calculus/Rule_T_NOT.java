package s4.s4tab.calculus;

import s4.s4tab.nodeset.Sign;
import s4.s4tab.nodeset.SignedFormula;
import s4.s4tab.nodeset._S4TabGoal;
import jtabwbx.modal.formula.ModalFormula;

/**
 * REGULAR RULE
 * 
 * <pre>
 *      S, T \neg A
 *    ------------------ 
 *       S, FA
 * </pre>
 */
public class Rule_T_NOT extends TabS4RegularRule {

  public Rule_T_NOT(_S4TabGoal premise, ModalFormula wff) {
    super(TabS4RuleIdentifiers.T_NOT, premise, new SignedFormula(Sign.T, wff), 1);
  }

  public _S4TabGoal conclusion(int i) {
    ModalFormula wff = mainFormula.getWff();
    _S4TabGoal conclusion = goal.clone();
    conclusion.removeSigned(Sign.T, wff);
    conclusion.addSigned(Sign.F, wff.immediateSubformulas()[0]);
    return conclusion;
  }

}
