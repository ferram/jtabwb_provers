package ipl.frj.sequent;

import ipl.frj.rules._FrjRule;
import jtabwbx.prop.basic.FormulaType;
import jtabwbx.prop.formula.BitSetOfFormulas;
import jtabwbx.prop.formula.Formula;

public class FrjIrregularSequent extends AbstractSequent {

  public FrjIrregularSequent(BitSetOfFormulas stable, BitSetOfFormulas nonstable, Formula right,
      int iteration, _FrjRule generatingRule) {
    super(SequentType.IRREGULAR, iteration, generatingRule);
    this.stable = stable;
    this.nonStable = nonstable;
    this.right = right;
    { // compute hashCode
      final int prime = 31;
      int result = 1;
      result = prime * result + ((this.sequentType == null) ? 0 : this.sequentType.hashCode());
      result = prime * result + ((this.nonStable == null) ? 0 : this.nonStable.hashCode());
      result = prime * result + ((this.stable == null) ? 0 : this.stable.hashCode());
      result = prime * result + ((this.right == null) ? 0 : this.right.hashCode());
      this.hashCode = result;
    }
  }

  private BitSetOfFormulas nonStable, stable, leftSide = null;
  private Formula right;
  private final int hashCode;

  @Override
  public boolean isRightEmtpy() {
    return right == null;
  }

  @Override
  public Formula right() {
    return right;
  }

  /**
   * Returns the non-stable part of this sequent.
   * 
   * @return the non-stable part of this sequent.
   */
  public BitSetOfFormulas nonStable() {
    return nonStable;
  }

  /**
   * Returns the bitset containing the formulas of the specified type in the
   * non-stable part of this irregular sequent.
   * 
   * @param the type off the formulas of the non-stable part to include in the
   * returned bitset.
   * @return the stable part of the sequent.
   */
  public BitSetOfFormulas nonStable(FormulaType type) {
    if (nonStable == null)
      return null;
    else
      return nonStable.getBitsetOfAllFormulas(type);
  }

  /**
   * Returns the stable part of this sequent.
   * 
   * @return the stable part of this sequent.
   */
  public BitSetOfFormulas stable() {
    return stable;
  }

  /**
   * Returns the bitset containing the formulas of the specified type in the
   * stable part of this irregular sequent.
   * 
   * @param the type off the formulas of the stable part to include in the
   * returned bitset.
   * @return the stable part of the sequent.
   */
  public BitSetOfFormulas stable(FormulaType type) {
    if (stable == null)
      return null;
    else
      return stable.getBitsetOfAllFormulas(type);
  }

  /**
   * Returns the left side of the sequent consisting of the union of stable and
   * non-stable part of the irregular sequent.
   * 
   * @return the left side of the irregular sequent.
   */
  @Override
  public BitSetOfFormulas left() {
    if (leftSide != null)
      return leftSide;
    else if (stable == null && nonStable == null)
      return null;
    else
      return leftSide = BitSetOfFormulas.orNullAsEmpty(stable, nonStable);
  }

  /**
   * Returns true iff this sequent subsumes other,
   * 
   * <pre>
   * S; T, T' --> H SUBSUMES S; T --> H
   * 
   * <pre>
   * </li>
   * </ul>
   * 
   * @param other
   * @return true iff this sequent subsumes other.
   */
  @Override
  public boolean subsumes(_FrjSequent other) {
    if (!(other instanceof FrjIrregularSequent))
      return false;

    if (!this.right.equals(other.right()))
      return false;

    FrjIrregularSequent oseq = (FrjIrregularSequent) other;

    if (this.stable != null || oseq.stable != null) {
      if ((this.stable == null && oseq.stable != null)
          || this.stable != null && oseq.stable == null)
        return false;

      if (!this.stable.equals(oseq.stable))
        return false;
    }

    if (oseq.nonStable == null)
      return true;
    if (this.nonStable == null)
      return false;

    return oseq.nonStable.subseteq(this.nonStable);
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FrjIrregularSequent other = (FrjIrregularSequent) obj;
    if (this.nonStable == null) {
      if (other.nonStable != null)
        return false;
    } else if (!this.nonStable.equals(other.nonStable))
      return false;
    if (this.stable == null) {
      if (other.stable != null)
        return false;
    } else if (!this.stable.equals(other.stable))
      return false;
    if (this.right == null) {
      if (other.right != null)
        return false;
    } else if (!this.right.equals(other.right))
      return false;
    return true;
  }

  static private String FMT_SEQ = "%s;%s ---> %s";

  @Override
  public String format() {
    return String.format(FMT_SEQ, (stable == null ? "" : stable.toString()),
        (nonStable == null ? "" : nonStable.toString()), right == null ? "" : right.format());
  }

  @Override
  public String toString() {
    return format(); // + " -- " + additionalInfo();
  }

  private String additionalInfo() {
    return "HashCode = " + hashCode + " [" //
        + (stable == null ? 0 : stable.hashCode()) //
        + (nonStable == null ? 0 : nonStable.hashCode()) //
        + (right == null ? 0 : right.hashCode())//
        + "]";

  }

}
