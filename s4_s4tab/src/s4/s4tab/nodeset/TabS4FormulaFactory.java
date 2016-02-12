package s4.s4tab.nodeset;

import java.io.Serializable;

import jtabwbx.modal.formula.ModalFormulaFactory;

/**
 * Formula factory for TabS4 formulas.
 * 
 * @author Mauro Ferrari
 */
public class TabS4FormulaFactory extends ModalFormulaFactory implements Serializable {

  public TabS4FormulaFactory() {
    super("false", "true"); // false is in the language, true is not
    super.setTranslateEquivalences(true); // IFF is not in the language
  }

  public String getDescription() {
    return "Formula factory for TabS4 formulas";
  }

}
