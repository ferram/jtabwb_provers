package ipl.frj.gbu.rules;

import ipl.frj.gbu.sequent.GbuRegularSequent;
import jtabwb.engine.NoSuchSubgoalException;
import jtabwb.engine._AbstractGoal;
import jtabwbx.prop.formula.Formula;

public class Reg_Left_OR extends AbstractRegularRule {

  private static final int NUMBER_OF_CONCLUSIONS = 2;

  public Reg_Left_OR(GbuRegularSequent goal, Formula mainFormula) {
    super(GbuRuleIdentifiers.REG_LEFT_OR, goal, mainFormula, NUMBER_OF_CONCLUSIONS);
  }

  @Override
  public _AbstractGoal subgoal(int i) throws NoSuchSubgoalException {
    switch (i) {
    case 0: {
      GbuRegularSequent subgoal = (GbuRegularSequent) goal().clone();
      subgoal.removeLeft(mainFormula);
      subgoal.addLeft(mainFormula.immediateSubformulas()[0]);
      return subgoal;
    }
    case 1: {
      GbuRegularSequent subgoal = (GbuRegularSequent) goal().clone();
      subgoal.removeLeft(mainFormula);
      subgoal.addLeft(mainFormula.immediateSubformulas()[1]);
      return subgoal;
    }
    default:
      throw new NoSuchSubgoalException();
    }
  }

}
