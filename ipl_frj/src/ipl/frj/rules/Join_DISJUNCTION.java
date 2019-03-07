package ipl.frj.rules;

import ipl.frj.sequent._FrjSequent;
import jtabwbx.prop.formula.BitSetOfFormulas;
import jtabwbx.prop.formula.Formula;

public class Join_DISJUNCTION extends AbstarctJoin {

  public Join_DISJUNCTION(int iteration, BitSetOfFormulas sigma, BitSetOfFormulas theta,
      Formula mainFormula, _FrjSequent... premises) {
    super(FrjRulesID.JOIN_DISJUNCTION, iteration, sigma, theta, mainFormula, premises);
  }

  public Join_DISJUNCTION(int iteration, JoinPremises jp, Formula mainFormula) {
    super(FrjRulesID.JOIN_DISJUNCTION, iteration, jp, mainFormula);
  }
}
