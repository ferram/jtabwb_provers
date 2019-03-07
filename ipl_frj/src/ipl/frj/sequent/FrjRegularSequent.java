package ipl.frj.sequent;

import ipl.frj.rules._FrjRule;
import jtabwbx.prop.basic.FormulaType;
import jtabwbx.prop.formula.BitSetOfFormulas;
import jtabwbx.prop.formula.Formula;

public class FrjRegularSequent extends AbstractSequent {

  public FrjRegularSequent(BitSetOfFormulas left, Formula right, int iteration,
      _FrjRule generatingRule) {
    super(SequentType.REGULAR, iteration, generatingRule);
    this.left = left;
    this.right = right;
    { // compute hashcode
      int prime = 31;
      int result = 1;
      result = prime * result + ((this.sequentType == null) ? 0 : this.sequentType.hashCode());
      result = prime * result + ((this.left == null) ? 0 : this.left.hashCode());
      result = prime * result + ((this.right == null) ? 0 : this.right.hashCode());
      this.hashCode = result;
    }
  }

  private BitSetOfFormulas left;
  private Formula right;
  private final int hashCode;

  @Override
  public BitSetOfFormulas left() {
    return left;
  }

  public BitSetOfFormulas nonStable(FormulaType type) {
    if (left == null)
      return null;
    else
      return left.getBitsetOfAllFormulas(type);
  }

  @Override
  public boolean isRightEmtpy() {
    return right == null;
  }

  @Override
  public Formula right() {
    return right;
  }

  /**
   * Returns true iff this sequent subsumes other.
   * 
   * @param other the other sequent.
   * @return true iff this sequent subsumes other
   */
  @Override
  public boolean subsumes(_FrjSequent other) {
    if (!(other instanceof FrjRegularSequent))
      return false;

    if (!this.right.equals(other.right()))
      return false;

    FrjRegularSequent oseq = (FrjRegularSequent) other;
    if (left == null)
      return true;
    if (oseq.left == null)
      return false;
    return oseq.left.subseteq(this.left);
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
    FrjRegularSequent other = (FrjRegularSequent) obj;
    if (this.hashCode != other.hashCode)
      return false;
    if (this.left == null) {
      if (other.left != null)
        return false;
    } else if (!this.left.equals(other.left))
      return false;
    if (this.right == null) {
      if (other.right != null)
        return false;
    } else if (!this.right.equals(other.right))
      return false;
    return true;
  }

  static private String FMT_SEQ = "%s ==> %s";

  @Override
  public String format() {
    return String.format(FMT_SEQ, (left == null ? "" : left.toString()),
        (right == null ? "" : right.format()));
  }

  @Override
  public String toString() {
    return format();
  }

}
