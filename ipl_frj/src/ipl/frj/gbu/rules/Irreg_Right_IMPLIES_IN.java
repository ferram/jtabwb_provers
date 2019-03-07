package ipl.frj.gbu.rules;

import ipl.frj.gbu.sequent.GbuIrregularSequent;
import jtabwb.engine.NoSuchSubgoalException;
import jtabwb.engine._AbstractGoal;
import jtabwbx.prop.formula.Formula;

public class Irreg_Right_IMPLIES_IN extends AbstractRegularRule {

  private static final int NUMBER_OF_CONCLUSIONS = 1;

  public Irreg_Right_IMPLIES_IN(GbuIrregularSequent goal, Formula mainFormula) {
    super(GbuRuleIdentifiers.IRREG_RIGHT_IMPLIES_IN, goal, mainFormula, NUMBER_OF_CONCLUSIONS);
  }

  @Override
  public _AbstractGoal subgoal(int i) throws NoSuchSubgoalException {
    GbuIrregularSequent subgoal = (GbuIrregularSequent) goal().clone();
    subgoal.addRight(mainFormula.immediateSubformulas()[1]);
    return subgoal;
  }

}
