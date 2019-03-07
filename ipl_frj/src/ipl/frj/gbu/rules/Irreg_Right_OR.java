package ipl.frj.gbu.rules;

import ipl.frj.gbu.sequent.GbuIrregularSequent;
import jtabwb.engine.NoSuchSubgoalException;
import jtabwb.engine._AbstractGoal;
import jtabwbx.prop.formula.Formula;

public class Irreg_Right_OR extends AbstractRegularRule {

  private static final int NUMBER_OF_CONCLUSIONS = 1;

  /**
   * An instance of a right or rule for regular sequents where the disjunct in
   * the right side of the subgoal is <code>selectedDisjunct</code>.
   * 
   * @param goal the goal sequent.
   * @param mainFormula the main formula of the rule.
   * @param selectedDisjunct the selected dsjunct to tret in the subgoal.
   */
  public Irreg_Right_OR(GbuIrregularSequent goal, Formula mainFormula, Formula selectedDisjunct) {
    super(GbuRuleIdentifiers.IRREG_RIGHT_OR, goal, mainFormula, NUMBER_OF_CONCLUSIONS);
    this.selectedDisjunct = selectedDisjunct;
  }

  private Formula selectedDisjunct;

  @Override
  public _AbstractGoal subgoal(int i) throws NoSuchSubgoalException {
    GbuIrregularSequent subgoal = (GbuIrregularSequent) goal().clone();
    subgoal.addRight(selectedDisjunct);
    return subgoal;
  }

}
