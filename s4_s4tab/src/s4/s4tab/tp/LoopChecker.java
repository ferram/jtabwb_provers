package s4.s4tab.tp;

import java.util.LinkedList;

import jtabwb.util.ImplementationError;
import s4.s4tab.nodeset._S4TabGoal;

/**
 * A loop checker.
 * 
 * @author Mauro Ferrari
 *
 */
class LoopChecker {

  public LoopChecker() {
    super();
    this.stack = new LinkedList<_S4TabGoal>();
  }

  private LinkedList<_S4TabGoal> stack;

  /**
   * Pushes the specified goal in the stack.
   * 
   * @param goal the gol to push.
   */
  public void push(_S4TabGoal goal) {
    stack.addLast(goal);
  }

  /**
   * This method is used to check that the loop-check strategy is correctly
   * applied. Remove the goal at the top of the stack checking that such a goal
   * is equal to the specified one, if this does not hold the method throws an
   * {@link ImplementationError}.
   * 
   * @param goal
   */
  public void pop(_S4TabGoal goal) {
    if (!stack.getLast().equals(goal))
      throw new ImplementationError("Pop on wrong element");
    stack.removeLast();
  }

  /**
   * Remove the goal at the top of the stack.
   * 
   * @param goal
   */
  public void pop() {
    stack.removeLast();
  }

  /**
   * Returns true iff the stack contains the specified goal.
   * 
   * @param goal the goal to search in the stack.
   * @return true iff the stack contains the specified goal.
   */
  public boolean contains(_S4TabGoal goal) {
    for (_S4TabGoal stored : stack)
      if (stored.equals(goal))
        return true;
    return false;
  }

}
