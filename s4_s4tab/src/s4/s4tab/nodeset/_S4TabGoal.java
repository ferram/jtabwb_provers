package s4.s4tab.nodeset;

import jtabwb.engine._AbstractGoal;
import jtabwbx.modal.basic.ModalFormulaType;
import jtabwbx.modal.formula.BitSetOfModalFormulas;
import jtabwbx.modal.formula.ModalFormula;

/**
 * An object implementing this interface is a set of signed formulas over the
 * langua of S4Tab.
 * 
 * @author Mauro Ferrari
 *
 */
public interface _S4TabGoal extends _AbstractGoal {

  public _S4TabGoal clone();

  public _S4TabGoal cloneStablePart();

  public void addSigned(Sign sign, ModalFormula swff);

  public boolean contains(Sign sign, ModalFormula wff);

  public boolean removeSigned(Sign sign, ModalFormula wff);

  public TabS4FormulaFactory getFormulaFactory();

  public void addAlreadyTreatedTcDuplication(ModalFormula wff);

  public BitSetOfModalFormulas getAlreadyTreatedDuplications();

  public BitSetOfModalFormulas getAllFormulasOfType(Sign sign, ModalFormulaType formulaType);

  public ModalFormula getFirstFormulaOfType(Sign sign, ModalFormulaType formulaType);

  public boolean checkForClash();

}
