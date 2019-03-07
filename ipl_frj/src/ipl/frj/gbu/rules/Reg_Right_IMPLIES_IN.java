package ipl.frj.gbu.rules;

import ipl.frj.gbu.sequent.GbuRegularSequent;
import jtabwb.engine.NoSuchSubgoalException;
import jtabwb.engine._AbstractGoal;
import jtabwbx.prop.formula.Formula;

public class Reg_Right_IMPLIES_IN extends AbstractRegularRule {

  private static final int NUMBER_OF_CONCLUSIONS = 1;

  public Reg_Right_IMPLIES_IN(GbuRegularSequent goal, Formula mainFormula) {
    super(GbuRuleIdentifiers.REG_RIGHT_IMPLIES_IN, goal, mainFormula, NUMBER_OF_CONCLUSIONS);
  }

  @Override
  public _AbstractGoal subgoal(int i) throws NoSuchSubgoalException {
    GbuRegularSequent subgoal = (GbuRegularSequent) goal().clone();
    subgoal.addRight(mainFormula.immediateSubformulas()[1]);
    return subgoal;
  }

}
