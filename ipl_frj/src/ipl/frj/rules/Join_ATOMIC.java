package ipl.frj.rules;

import ipl.frj.sequent._FrjSequent;
import jtabwbx.prop.formula.BitSetOfFormulas;
import jtabwbx.prop.formula.Formula;

public class Join_ATOMIC extends AbstarctJoin {

  public Join_ATOMIC(int iteration, BitSetOfFormulas sigma, BitSetOfFormulas theta,
      Formula mainFormula, _FrjSequent... premises) {
    super(FrjRulesID.JOIN_ATOMIC, iteration, sigma, theta, mainFormula, premises);
  }

  public Join_ATOMIC(int iteration, JoinPremises jp, Formula mainFormula) {
    super(FrjRulesID.JOIN_ATOMIC, iteration, jp, mainFormula);
  }
}
