package s4.s4tab.nodeset;

import jtabwb.engine._AbstractFormula;
import jtabwbx.modal.formula.ModalFormula;

/**
 * A signed formula of S4Tab.
 * @author Mauro Ferrari
 *
 */
public class SignedFormula implements _AbstractFormula {

  public SignedFormula(Sign sign, ModalFormula wff) {
    super();
    this.sign = sign;
    this.wff = wff;
  }

  private Sign sign;
  private ModalFormula wff;

  /**
   * @return the sign
   */
  public Sign getSign() {
    return this.sign;
  }

  /**
   * @return the wff
   */
  public ModalFormula getWff() {
    return this.wff;
  }

  @Override
  public String format() {
    return sign.name() + "[" + wff.format() + "]";
  }

  @Override
  public String shortName() {
    return sign.name() + (wff.isAtomic() ? "-atomic" : wff.mainConnective().getName());
  }

}
