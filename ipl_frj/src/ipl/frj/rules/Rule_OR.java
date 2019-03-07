package ipl.frj.rules;

import ipl.frj.sequent.FrjIrregularSequent;
import ipl.frj.sequent._FrjSequent;
import jtabwbx.prop.formula.BitSetOfFormulas;
import jtabwbx.prop.formula.Formula;

public class Rule_OR extends AbstractRule {

  public Rule_OR(int iteration, Formula mainFormula, _FrjSequent... premises) {
    super(iteration, FrjRulesID.OR_IRR, mainFormula, premises);

    FrjIrregularSequent left_premise = (FrjIrregularSequent) premises[0];
    FrjIrregularSequent right_premise = (FrjIrregularSequent) premises[1];

    BitSetOfFormulas res =
        BitSetOfFormulas.orNullAsEmpty(left_premise.stable(), right_premise.stable());
    BitSetOfFormulas left =
        BitSetOfFormulas.andNullAsEmpty(left_premise.nonStable(), right_premise.nonStable());

    conclusion = new FrjIrregularSequent(res, left, mainFormula, iteration, this);
  }

  FrjIrregularSequent conclusion;

  @Override
  public FrjIrregularSequent conclusion() {
    return conclusion;
  }

}
