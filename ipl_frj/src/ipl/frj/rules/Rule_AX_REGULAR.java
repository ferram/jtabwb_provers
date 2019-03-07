package ipl.frj.rules;

import ipl.frj.sequent.FrjRegularSequent;
import jtabwbx.prop.formula.BitSetOfFormulas;
import jtabwbx.prop.formula.Formula;

public class Rule_AX_REGULAR extends AbstractAx {

  public Rule_AX_REGULAR(int iteration, BitSetOfFormulas left, Formula right) {
    super(iteration, FrjRulesID.AX_REG, right);
    left = left.clone();
    left.remove(right);
    this.conclusion = new FrjRegularSequent(left, right, iteration, this);
  }

  private FrjRegularSequent conclusion;

  @Override
  public FrjRegularSequent conclusion() {
    return conclusion;
  }

}
