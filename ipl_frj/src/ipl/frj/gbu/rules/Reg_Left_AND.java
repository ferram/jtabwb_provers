package ipl.frj.gbu.rules;

import ipl.frj.gbu.sequent.GbuRegularSequent;
import jtabwb.engine.NoSuchSubgoalException;
import jtabwb.engine._AbstractGoal;
import jtabwbx.prop.formula.Formula;

public class Reg_Left_AND extends AbstractRegularRule {

  private static final int NUMBER_OF_CONCLUSIONS = 1;

  public Reg_Left_AND(GbuRegularSequent goal, Formula mainFormula) {
    super(GbuRuleIdentifiers.REG_LEFT_AND, goal, mainFormula, NUMBER_OF_CONCLUSIONS);
  }

  @Override
  public _AbstractGoal subgoal(int i) throws NoSuchSubgoalException {
    GbuRegularSequent subgoal = (GbuRegularSequent) goal().clone();
    subgoal.removeLeft(mainFormula);
    subgoal.addLeft(mainFormula.immediateSubformulas()[0]);
    subgoal.addLeft(mainFormula.immediateSubformulas()[1]);
    return subgoal;
  }

}
