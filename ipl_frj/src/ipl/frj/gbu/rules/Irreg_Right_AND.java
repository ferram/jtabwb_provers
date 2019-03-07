package ipl.frj.gbu.rules;

import ipl.frj.gbu.sequent.GbuIrregularSequent;
import jtabwb.engine.NoSuchSubgoalException;
import jtabwb.engine._AbstractGoal;
import jtabwbx.prop.formula.Formula;

public class Irreg_Right_AND extends AbstractRegularRule {

  private static final int NUMBER_OF_CONCLUSIONS = 2;

  public Irreg_Right_AND(GbuIrregularSequent goal, Formula mainFormula) {
    super(GbuRuleIdentifiers.IRREG_RIGHT_AND, goal, mainFormula, NUMBER_OF_CONCLUSIONS);
  }

  @Override
  public _AbstractGoal subgoal(int i) throws NoSuchSubgoalException {
    switch (i) {
    case 0: {
      GbuIrregularSequent subgoal = (GbuIrregularSequent) goal().clone();
      subgoal.addRight(mainFormula.immediateSubformulas()[0]);
      return subgoal;
    }
    case 1: {
      GbuIrregularSequent subgoal = (GbuIrregularSequent) goal().clone();
      subgoal.addRight(mainFormula.immediateSubformulas()[1]);
      return subgoal;
    }
    default:
      throw new NoSuchSubgoalException();
    }
  }

}
