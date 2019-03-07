package ipl.frj.rules;

import ipl.frj.sequent.FrjIrregularSequent;
import jtabwbx.prop.formula.BitSetOfFormulas;
import jtabwbx.prop.formula.Formula;

public class Rule_IMPLIES_IRREGULAR_IN_CL extends AbstractRule {

  public Rule_IMPLIES_IRREGULAR_IN_CL(int iteration, BitSetOfFormulas moveToResources,
      Formula mainFormula, FrjIrregularSequent... premises) {
    super(iteration, FrjRulesID.IMP_IRR_IN_CL, mainFormula, premises);

    // build new resources
    BitSetOfFormulas newResources =
        BitSetOfFormulas.orNullAsEmpty(((FrjIrregularSequent) premises[0]).stable(), moveToResources);
    // build new left
    BitSetOfFormulas newLeft;
    if (((FrjIrregularSequent) premises[0]).nonStable() == null)
      newLeft = null;
    else if (moveToResources == null)
      newLeft = ((FrjIrregularSequent) premises()[0]).nonStable();
    else
      newLeft = ((FrjIrregularSequent) premises[0]).nonStable().difference(moveToResources);

    conclusion = new FrjIrregularSequent(newResources, newLeft, mainFormula, iteration, this);
  }

  private FrjIrregularSequent conclusion;

  @Override
  public FrjIrregularSequent conclusion() {
    return conclusion;
  }

}
