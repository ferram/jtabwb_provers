package ipl.frj.rules;

import ipl.frj.sequent._FrjSequent;
import jtabwbx.prop.formula.Formula;

public abstract class AbstractAx extends AbstractRule {

  public AbstractAx(int iteration, FrjRulesID ruleID, Formula mainFormula, _FrjSequent... premises) {
    super(iteration, ruleID, mainFormula, premises);
  }

}
