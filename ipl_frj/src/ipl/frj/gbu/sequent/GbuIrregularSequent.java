package ipl.frj.gbu.sequent;

import ipl.frj.sequent.FrjFormulaFactory;
import ipl.frj.sequent.SequentType;
import jtabwbx.prop.formula.SingleSuccedentSequentOnBSF;

public class GbuIrregularSequent extends SingleSuccedentSequentOnBSF implements _GbuSequent {

  public GbuIrregularSequent(FrjFormulaFactory factory) {
    super(factory);
  }

  @Override
  public boolean isIrregular() {
    return true;
  }

  @Override
  public boolean isRegular() {
    return false;
  }

  @Override
  public SequentType type() {
    return SequentType.IRREGULAR;
  }
  
  public GbuIrregularSequent clone() {
    return (GbuIrregularSequent) super.clone();
  }

  static private String FMT_SEQ = "%s --> %s";

  @Override
  public String format() {
    return String.format(FMT_SEQ, (this.isLeftSideEmpty() ? "" : this.leftSide().toString()),
        (this.getRight() == null ? "" : this.getRight().format()));
  }

}
