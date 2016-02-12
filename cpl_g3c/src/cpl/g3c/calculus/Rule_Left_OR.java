package cpl.g3c.calculus;

import jtabwb.engine.NoSuchSubgoalException;
import jtabwb.engine._AbstractFormula;
import jtabwb.engine._RegularRule;
import jtabwbx.prop.formula.Formula;
import jtabwbx.prop.formula._Sequent;

/**
 * <pre>
 *     A , S  ==> T   B , S  ==> T  
 *    ----------------------------- (L_OR)
 *     A OR B , S  ==> T
 * 
 * </pre>
 * 
 * @author Mauro Ferrari
 */
public class Rule_Left_OR implements _RegularRule {

  private _Sequent premise;
  private Formula mainFormula;
  private int nextConclusionIndex = 0;
  private final int NUMBER_OF_CONCLUSIONS = 2;


  public Rule_Left_OR(_Sequent premise, Formula mainFormula) {
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
    result.removeLeft(mainFormula);
    Formula[] subformulas = mainFormula.immediateSubformulas();
    result.addLeft(subformulas[nextConclusionIndex++]);
    return result;
  }

  @Override
  public String name() {
    return "LEFT_OR";
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
