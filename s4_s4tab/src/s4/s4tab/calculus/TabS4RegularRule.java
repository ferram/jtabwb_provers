package s4.s4tab.calculus;

import s4.s4tab.nodeset.SignedFormula;
import s4.s4tab.nodeset._S4TabGoal;
import jtabwb.engine.NoSuchSubgoalException;
import jtabwb.engine._RegularRule;

/**
 * The super-type for TabS4 regular rules.
 * 
 * @author Mauro Ferrari
 */
abstract class TabS4RegularRule implements _RegularRule, _TabS4AbstractRule {

  private TabS4RuleIdentifiers ruleIdentifier;
  protected _S4TabGoal goal;
  protected SignedFormula mainFormula;
  private final int numberOfConclusions;
  private int nextConclusionIndex = 0;

  protected TabS4RegularRule(TabS4RuleIdentifiers ruleIdentifier, _S4TabGoal premise,
      SignedFormula mainFormula, int numberOfConclusions) {
    super();
    this.ruleIdentifier = ruleIdentifier;
    this.goal = premise;
    this.mainFormula = mainFormula;
    this.numberOfConclusions = numberOfConclusions;
  }

  @Override
  public String name() {
    return ruleIdentifier.name();
  }

  public _S4TabGoal goal() {
    return goal;
  }

  @Override
  public int numberOfSubgoals() {
    return numberOfConclusions;
  }

  @Override
  public SignedFormula mainFormula() {
    return mainFormula;
  }

  @Override
  public TabS4RuleIdentifiers getRuleIdentifier() {
    return ruleIdentifier;
  }

  @Override
  public boolean hasNextSubgoal() {
    return nextConclusionIndex < numberOfConclusions;
  }

  @Override
  public _S4TabGoal nextSubgoal() {
    return conclusion(nextConclusionIndex++);
  }

  /**
   * Returns the specified conclusion. The index of conclusions is zero-based,
   * hence the first branch has index <code>0</code>, and the last has index
   * <code>numberOfConclusions() - 1</code>.
   * 
   * @return the specified conclusion.
   * @throws NoSuchSubgoalException if the specified conclusion does not
   * exist.
   */
  public abstract _S4TabGoal conclusion(int i) throws NoSuchSubgoalException;

}
