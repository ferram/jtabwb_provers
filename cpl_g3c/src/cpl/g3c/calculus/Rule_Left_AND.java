package cpl.g3c.calculus;


import jtabwb.engine.NoSuchSubgoalException;
import jtabwb.engine._AbstractFormula;
import jtabwb.engine._RegularRule;
import jtabwbx.prop.formula.Formula;
import jtabwbx.prop.formula._Sequent;

/**
 * <pre>
 *     A , B , S  ==> T  
 *    --------------------- (L_AND)
 *     A AND B , S  ==> T
 * 
 * </pre>
 * 
 * @author Mauro Ferrari
 */
public class Rule_Left_AND implements _RegularRule {

  private _Sequent premise;
  private Formula mainFormula;
  private int nextConclusionIndex = 0;
  private final int NUMBER_OF_CONCLUSIONS = 1; 

  public Rule_Left_AND(_Sequent premise, Formula mainFormula) {
    super();
    this.premise = premise;
    this.mainFormula = mainFormula;
  }

  @Override
  public boolean hasNextSubgoal() {
    return nextConclusionIndex < NUMBER_OF_CONCLUSIONS;
  }

  @Override
  public _Sequent nextSubgoal() {
    if (nextConclusionIndex >= NUMBER_OF_CONCLUSIONS)
      throw new NoSuchSubgoalException();
    nextConclusionIndex++;
    _Sequent conclusion = premise.clone();
    Formula[] subformulas = mainFormula.immediateSubformulas();
    conclusion.removeLeft(mainFormula);
    conclusion.addLeft(subformulas[0]);
    conclusion.addLeft(subformulas[1]);
    return conclusion;
  }

  @Override
  public String name() {
    return "LEFT_AND";
  }

  @Override
  public int numberOfSubgoals() {
    return NUMBER_OF_CONCLUSIONS;
  }

  @Override
  public _AbstractFormula mainFormula() {
    return mainFormula;
  }

}
