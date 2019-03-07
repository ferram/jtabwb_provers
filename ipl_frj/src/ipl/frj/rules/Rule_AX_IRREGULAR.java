package ipl.frj.rules;

import ipl.frj.sequent.FrjIrregularSequent;
import jtabwbx.prop.formula.BitSetOfFormulas;
import jtabwbx.prop.formula.Formula;

public class Rule_AX_IRREGULAR extends AbstractAx {

  public Rule_AX_IRREGULAR(int iteration, BitSetOfFormulas left, Formula right) {
    super(iteration, FrjRulesID.AX_IRR, right);
    left = left.clone();
    left.remove(right);
    this.conclusion = new FrjIrregularSequent(null, left, right, iteration, this);
  }

  private FrjIrregularSequent conclusion;

  @Override
  public FrjIrregularSequent conclusion() {
    return conclusion;
  }

}
