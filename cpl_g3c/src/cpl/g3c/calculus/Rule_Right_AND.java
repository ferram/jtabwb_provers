package cpl.g3c.calculus;


import jtabwb.engine.NoSuchSubgoalException;
import jtabwb.engine._AbstractFormula;
import jtabwb.engine._RegularRule;
import jtabwbx.prop.formula.Formula;
import jtabwbx.prop.formula._Sequent;

/**
 * <pre>
 *      S  ==> A, T    S  ==> B, T  
 *    ----------------------------- (R_AND)
 *           S  ==> A AND  B, T
 * 
 * </pre>
 * 
 * @author Mauro Ferrari
 */
public class Rule_Right_AND implements _RegularRule {

  private _Sequent premise;
  private Formula mainFormula;
  private int nextConclusionIndex = 0;
  private final int NUMBER_OF_CONCLUSIONS = 2;

  public Rule_Right_AND(_Sequent premise, Formula mainFormula) {
    super();
    this.premise = premise;
    this.mainFormula = mainFormula;
  }

  @Override
  public boolean hasNextSubgoal() {
    return nextConclusionIndex < NUMBER_OF_CONCLUSIONS;
  }

  @Override
  public _Sequent nextSubgoal() throws NoSuchSubgoalException {
    if (nextConclusionIndex >= NUMBER_OF_CONCLUSIONS)
      throw new NoSuchSubgoalException();
    _Sequent result = premise.clone();
    result.removeRight(mainFormula);
    Formula[] subformulas = mainFormula.immediateSubformulas();
    result.addRight(subformulas[nextConclusionIndex++]);
    return result;
  }

  @Override
  public String name() {
    return "RIGHT_AND";
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
