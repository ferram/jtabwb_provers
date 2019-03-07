package ipl.frj.gbu.rules;

import ipl.frj.gbu.sequent.GbuIrregularSequent;
import ipl.frj.gbu.sequent.GbuRegularSequent;
import ipl.frj.sequent.FrjFormulaFactory;
import jtabwb.engine.NoSuchSubgoalException;
import jtabwb.engine._AbstractGoal;
import jtabwbx.prop.formula.Formula;

public class Irreg_Right_IMPLIES_NOT_IN extends AbstractRegularRule {

  private static final int NUMBER_OF_CONCLUSIONS = 1;

  public Irreg_Right_IMPLIES_NOT_IN(GbuIrregularSequent goal, Formula mainFormula) {
    super(GbuRuleIdentifiers.IRREG_RIGHT_IMPLIES_NOT_IN, goal, mainFormula, NUMBER_OF_CONCLUSIONS);
  }

  @Override
  public _AbstractGoal subgoal(int i) throws NoSuchSubgoalException {
    GbuIrregularSequent goal = (GbuIrregularSequent) goal();
    GbuRegularSequent subgoal = new GbuRegularSequent((FrjFormulaFactory) goal.getFormulaFactory());
    subgoal.addLeftAll(goal.leftSide());
    subgoal.addLeft(mainFormula.immediateSubformulas()[0]);
    subgoal.addRight(mainFormula.immediateSubformulas()[1]);
    return subgoal;
  }

}
