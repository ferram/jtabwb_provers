package s4.s4tab.calculus;

import s4.s4tab.nodeset.Sign;
import s4.s4tab.nodeset.SignedFormula;
import s4.s4tab.nodeset._S4TabGoal;
import jtabwbx.modal.formula.ModalFormula;

/**
 * REGULAR RULE
 * 
 * <pre>
 *      S, F \neg A
 *    ------------------ 
 *       S, TA
 * </pre>
 */
public class Rule_F_NOT extends TabS4RegularRule {

  public Rule_F_NOT(_S4TabGoal premise, ModalFormula wff) {
    super(TabS4RuleIdentifiers.F_NOT, premise, new SignedFormula(Sign.F, wff), 1);
  }

  public _S4TabGoal conclusion(int i) {
    ModalFormula wff = mainFormula.getWff();
    _S4TabGoal conclusion = goal.clone();
    conclusion.removeSigned(Sign.F, wff);
    conclusion.addSigned(Sign.T, wff.immediateSubformulas()[0]); // TA
    return conclusion;
  }

}
