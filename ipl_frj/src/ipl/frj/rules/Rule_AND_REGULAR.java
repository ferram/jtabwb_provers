package ipl.frj.rules;

import ipl.frj.sequent.FrjRegularSequent;
import jtabwbx.prop.formula.Formula;

public class Rule_AND_REGULAR extends AbstractRule {

  public Rule_AND_REGULAR(int iteration, Formula mainFormula, FrjRegularSequent... premises) {
    super(iteration, FrjRulesID.AND_REG, mainFormula, premises);
    this.conclusion = new FrjRegularSequent(premises[0].left(), mainFormula, iteration, this);
  }

  private FrjRegularSequent conclusion;

  @Override
  public FrjRegularSequent conclusion() {
    return conclusion;
  }

}
