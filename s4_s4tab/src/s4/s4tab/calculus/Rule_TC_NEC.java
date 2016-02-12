package s4.s4tab.calculus;

import s4.s4tab.nodeset.Sign;
import s4.s4tab.nodeset.SignedFormula;
import s4.s4tab.nodeset._S4TabGoal;
import jtabwbx.modal.formula.ModalFormula;

/**
 * REGULAR RULE
 * 
 * <pre>
 *      S, Tc \nec A
 *    ------------------ 
 *       S, TcA
 * </pre>
 */
public class Rule_TC_NEC extends TabS4RegularRule {

  public Rule_TC_NEC(_S4TabGoal premise, ModalFormula wff) {
    super(TabS4RuleIdentifiers.Tc_NEC, premise, new SignedFormula(Sign.Tc, wff), 1);
  }

  public _S4TabGoal conclusion(int i) {
    ModalFormula wff = mainFormula.getWff();
    _S4TabGoal conclusion = goal.clone();
    conclusion.removeSigned(Sign.Tc, wff);
    conclusion.addSigned(Sign.Tc, wff.immediateSubformulas()[0]); // TcA
    return conclusion;
  }

}
