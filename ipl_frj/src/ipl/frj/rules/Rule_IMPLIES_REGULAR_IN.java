package ipl.frj.rules;

import ipl.frj.sequent.FrjRegularSequent;
import jtabwbx.prop.formula.Formula;

public class Rule_IMPLIES_REGULAR_IN extends AbstractRule {

  public Rule_IMPLIES_REGULAR_IN(int iteration, Formula mainFormula, FrjRegularSequent... premises) {
    super(iteration, FrjRulesID.IMP_REG_IN_CL, mainFormula, premises);
    this.conclusion = new FrjRegularSequent(premises[0].left(), mainFormula, iteration, this);
  }

  private FrjRegularSequent conclusion;

  @Override
  public FrjRegularSequent conclusion() {
    return conclusion;
  }

}
