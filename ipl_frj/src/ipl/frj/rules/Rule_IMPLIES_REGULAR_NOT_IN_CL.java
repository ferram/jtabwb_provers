package ipl.frj.rules;

import ipl.frj.sequent.FrjIrregularSequent;
import ipl.frj.sequent._FrjSequent;
import jtabwbx.prop.formula.BitSetOfFormulas;
import jtabwbx.prop.formula.Formula;

public class Rule_IMPLIES_REGULAR_NOT_IN_CL extends AbstractRule {

  public Rule_IMPLIES_REGULAR_NOT_IN_CL(int iteration, BitSetOfFormulas theta, Formula mainFormula, _FrjSequent... premises) {
    super(iteration, FrjRulesID.IMP_REG_NOT_IN_CL, mainFormula, premises);
    this.theta = theta;
    this.conclusion = new FrjIrregularSequent(null, theta, mainFormula, iteration, this);
  }

  private BitSetOfFormulas theta;
  private FrjIrregularSequent conclusion;
  
  
  @Override
  public FrjIrregularSequent conclusion() {
    return conclusion;
  }

}
