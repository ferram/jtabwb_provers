package cpl.g3c.calculus;


import jtabwb.engine.NoSuchSubgoalException;
import jtabwb.engine._AbstractFormula;
import jtabwb.engine._RegularRule;
import jtabwbx.prop.formula.Formula;
import jtabwbx.prop.formula._Sequent;

/**
 * <pre>
 *        S  ==> A, B, T  
 *    --------------------- (R_OR)
 *      S  ==> A OR B, T
 * 
 * </pre>
 * 
 * @author Mauro Ferrari
 */
public class Rule_Right_OR implements _RegularRule {

  private _Sequent premise;
  private Formula mainFormula;
  private int nextConclusionIndex = 0;
  private final int NUMBER_OF_CONCLUSIONS = 1;

  public Rule_Right_OR(_Sequent premise, Formula mainFormula) {
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
    nextConclusionIndex++;
    premise.removeRight(mainFormula);
    Formula[] subformulas = mainFormula.immediateSubformulas();
    premise.addRight(subformulas[0]);
    premise.addRight(subformulas[1]);
    return premise;
  }

  @Override
  public String name() {
    return "RIGHT_OR";
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
