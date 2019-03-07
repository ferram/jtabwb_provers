package ipl.frj.gbu.rules;

import ipl.frj.gbu.sequent._GbuSequent;
import jtabwb.engine.NoSuchSubgoalException;
import jtabwb.engine._AbstractGoal;
import jtabwb.engine._RegularRule;
import jtabwbx.prop.formula.Formula;

/**
 * DOC
 * 
 * @author Mauro Ferrari
 */
abstract class AbstractRegularRule implements _RegularRule, _GbuRule {

  private GbuRuleIdentifiers identifier;
  protected _GbuSequent goal;
  protected Formula mainFormula;
  private final int NUMBER_OF_CONCLUSIONS;
  private int nextConclusionIndex = 0;

  public AbstractRegularRule(GbuRuleIdentifiers identifier, _GbuSequent goal,
      Formula mainFormula, int numberOfConclusions) {
    super();
    this.identifier = identifier;
    this.goal = goal;
    this.mainFormula = mainFormula;
    this.NUMBER_OF_CONCLUSIONS = numberOfConclusions;
  }

  @Override
  public String name() {
    return identifier.name();
  }

  @Override
  public _GbuSequent goal() {
    return goal;
  }

  @Override
  public int numberOfSubgoals() {
    return NUMBER_OF_CONCLUSIONS;
  }

  @Override
  public Formula mainFormula() {
    return mainFormula;
  }

  @Override
  public GbuRuleIdentifiers getRuleIdentifier() {
    return identifier;
  }

  @Override
  public boolean hasNextSubgoal() {
    return nextConclusionIndex < NUMBER_OF_CONCLUSIONS;
  }

  @Override
  public _AbstractGoal nextSubgoal() {
    return subgoal(nextConclusionIndex++);
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
  public abstract _AbstractGoal subgoal(int i) throws NoSuchSubgoalException;
}
