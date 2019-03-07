package ipl.frj.sequent;

import java.util.HashSet;

import ipl.frj.rules._FrjRule;
import jtabwbx.prop.formula.BitSetOfFormulas;
import jtabwbx.prop.formula.Formula;

/**
 * FRJ has two types of sequents:
 * 
 * <pre>
 * irregular sequents:  stable ; nonStable ---> right
 * regular sequents    left ==> right
 * where;
 * - stable, nonStable and left are sets of atomic and implicative formulas
 * - in irregular sequents the left side is the union if stable and non-stable formulas
 * - right is a formula or (null)
 * </pre>
 * 
 * @author Mauro Ferrari
 *
 */
public interface _FrjSequent {

  /**
   * Returns the global index of this sequent.
   * 
   * @return
   */
  public int getSequentProgessiveNumber();

  /**
   * Set the index of this sequent.
   * 
   * @param index
   */
  public void setSequentProgressiveNumber(int index);

  /**
   * The bitset of the left formulas of the sequent or null if the left side is
   * empty.
   * 
   * @return the left formulas of the sequent or null.
   */
  public BitSetOfFormulas left();

  /**
   * Returns true iff this is an irregular sequent.
   * 
   * @return true iff this is an irregular sequent.
   */
  public boolean isIrregular();

  /**
   * Returns true iff this is n regular sequent.
   * 
   * @return true iff this is n regular sequent.
   */
  public boolean isRegular();

  /**
   * Returns true iff the right side is empty.
   * 
   * @return true iff the right side is empty.
   */
  public boolean isRightEmtpy();

  /**
   * Returns the formula in the right side or null is the right side is empty.
   * 
   * @return the formula in the right side or null.
   */
  public Formula right();

  /**
   * Returns the type of this sequent.
   * 
   * @return the type of this sequent.
   */
  public SequentType type();

  /**
   * Returns a string representing this sequent.
   * 
   * @return a string representing this sequent.
   */
  public String format();

  /**
   * The iteration it which the sequent was generated;
   * 
   * @return iteration
   */
  public int iteration();

  /**
   * The rule generating this sequent.
   * 
   * @return the rule.
   */
  public _FrjRule generatingRule();

  /**
   * Returns true iff this sequent subsumes other.
   * @param other
   * @return true iff this sequent subsumes other.
   */
  public boolean subsumes(_FrjSequent other);
  
  public HashSet<_FrjSequent> getDependencies();

}
