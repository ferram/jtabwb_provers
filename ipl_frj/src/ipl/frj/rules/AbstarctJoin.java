package ipl.frj.rules;

import ipl.frj.sequent.FrjRegularSequent;
import ipl.frj.sequent._FrjSequent;
import jtabwbx.prop.formula.BitSetOfFormulas;
import jtabwbx.prop.formula.Formula;

public class AbstarctJoin extends AbstractRule {

  public AbstarctJoin(FrjRulesID join_rule, int iteration, BitSetOfFormulas sigma,
      BitSetOfFormulas theta, Formula mainFormula, _FrjSequent... premises) {
    super(iteration, join_rule, mainFormula, premises);

    BitSetOfFormulas left;
    if (sigma == null && theta == null)
      left = null;
    else if (sigma == null) {
      left = theta.clone();
      left.remove(mainFormula);
    } else if (theta == null)
      left = sigma.clone();
    else {
      left = sigma.clone();
      left.or(theta);
      left.remove(mainFormula);
    }
    conclusion = new FrjRegularSequent(left, mainFormula, iteration, this);
  }

  public AbstarctJoin(FrjRulesID join_rule, int iteration, JoinPremises jp, Formula mainFormula) {
    this(join_rule, iteration, jp.getSigma(), jp.getTheta(), mainFormula, jp.getPremises());
  }

  FrjRegularSequent conclusion;

  @Override
  final public FrjRegularSequent conclusion() {
    return conclusion;
  }

}
