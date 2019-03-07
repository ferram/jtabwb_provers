package ipl.frj.rules;

import ipl.frj.sequent.FrjIrregularSequent;
import jtabwbx.prop.formula.Formula;

public class Rule_AND_IRREGULAR extends AbstractRule {

  public Rule_AND_IRREGULAR(int iteration, Formula mainFormula, int treatedConjunct,
      FrjIrregularSequent... premises) {
    super(iteration, FrjRulesID.AND_IRR, mainFormula, premises);
    conclusion = new FrjIrregularSequent(((FrjIrregularSequent) premises[0]).stable(),
        ((FrjIrregularSequent) premises[0]).nonStable(), mainFormula, iteration, this);

  }

  private FrjIrregularSequent conclusion;

  @Override
  public FrjIrregularSequent conclusion() {
    return conclusion;
  }

}
