package s4.s4tab.calculus;

import s4.s4tab.nodeset.Sign;
import s4.s4tab.nodeset.SignedFormula;
import s4.s4tab.nodeset._S4TabGoal;
import jtabwbx.modal.basic.ModalConnective;
import jtabwbx.modal.formula.ModalFormula;

/**
 * REGULAR RULE, DUPLICATION
 * 
 * <pre>
 *      S, Tc \neg A
 *    ------------------ 
 *       S, FA, Tc\neg A   
 *       
 *       
 *  duplication is not needed if A is atomic or negated.
 *  if A is negated we get the conclusion S, Tc A
 * </pre>
 */
public class Rule_TC_NOT_duplication extends TabS4RegularRule {

  public Rule_TC_NOT_duplication(_S4TabGoal premise, ModalFormula wff) {
    super(TabS4RuleIdentifiers.Tc_NOT, premise, new SignedFormula(Sign.Tc, wff), 1);
  }

  public _S4TabGoal conclusion(int i) {
    ModalFormula wff = mainFormula.getWff();
    ModalFormula subformula = wff.immediateSubformulas()[0];
    _S4TabGoal conclusion = goal.clone();
    if (subformula.isCompound() && subformula.mainConnective() == ModalConnective.NOT) {
      conclusion.removeSigned(Sign.Tc, wff);
      conclusion.addSigned(Sign.Tc, subformula.immediateSubformulas()[0]);
    } else { // DUPLICATION
      conclusion.addAlreadyTreatedTcDuplication(wff);
      conclusion.addSigned(Sign.F, wff.immediateSubformulas()[0]); // FA
    }
    return conclusion;
  }

}
