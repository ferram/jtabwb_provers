package ipl.frj.gbu.rules;

import ipl.frj.gbu.sequent._GbuSequent;
import jtabwb.engine._AbstractRule;

/**
 * An object modeling a Gbu rule.
 * 
 * @author Mauro Ferrari
 *
 */
public interface _GbuRule extends _AbstractRule {

  /**
   * Returns the rule identifier.
   * 
   * @return the rule identifier.
   */
  public GbuRuleIdentifiers getRuleIdentifier();

  /**
   * Returns the goal of this rule.
   * 
   * @return the goal of this rule.
   */
  public _GbuSequent goal();
}
