package cpl.g3c.calculus;


import jtabwb.engine.NoSuchSubgoalException;
import jtabwb.engine._AbstractFormula;
import jtabwb.engine._RegularRule;
import jtabwbx.prop.formula.Formula;
import jtabwbx.prop.formula._Sequent;

/**
 * <pre>
 *      S  ==> A, T    B, S  ==> T  
 *    ----------------------------- (L_IMPLIES)
 *     A IMPLIES B , S  ==> T
 * </pre>
 * 
 * @author Mauro Ferrari
 */
public class Rule_Left_IMPLIES implements _RegularRule {

  private _Sequent premise;
  private Formula mainFormula;
  private int nextConclusionIndex = 0;
  private final int NUMBER_OF_CONCLUSIONS = 2;

  public Rule_Left_IMPLIES(_Sequent premise, Formula mainFormula) {
    super();
    this.premise = premise;
    this.mainFormula = mainFormula;
  }

  @Override
  public boolean hasNextSubgoal() {
    return nextConclusionIndex < NUMBER_OF_CONCLUSIONS;
  }

  public _Sequent nextSubgoal() throws NoSuchSubgoalException {
    _Sequent result = premise.clone();
    result.removeLeft(mainFormula);
    Formula[] subformulas = mainFormula.immediateSubformulas();
    switch (nextConclusionIndex) {
    case 0: {
      result.addRight(subformulas[0]);
      nextConclusionIndex++;
      return result;
    }
    case 1: {
      result.addLeft(subformulas[1]);
      nextConclusionIndex++;
      return result;
    }
    default:
      throw new NoSuchSubgoalException();
    }

  }

  @Override
  public String name() {
    return "LEFT_IMPLIES";
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
