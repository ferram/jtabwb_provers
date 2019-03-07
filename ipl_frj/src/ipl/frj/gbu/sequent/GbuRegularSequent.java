package ipl.frj.gbu.sequent;

import ipl.frj.sequent.FrjFormulaFactory;
import ipl.frj.sequent.SequentType;
import jtabwbx.prop.formula.SingleSuccedentSequentOnBSF;

public class GbuRegularSequent extends SingleSuccedentSequentOnBSF implements _GbuSequent {

  public GbuRegularSequent(FrjFormulaFactory factory) {
    super(factory);
  }

  @Override
  public boolean isIrregular() {
    return false;
  }

  @Override
  public boolean isRegular() {
    return true;
  }

  @Override
  public SequentType type() {
    return SequentType.REGULAR;
  }

  public GbuRegularSequent clone() {
    return (GbuRegularSequent) super.clone();
  }

  static private String FMT_SEQ = "%s ==> %s";

  @Override
  public String format() {
    return String.format(FMT_SEQ, (this.isLeftSideEmpty() ? "" : this.leftSide().toString()),
        (this.getRight() == null ? "" : this.getRight().format()));
  }

}
