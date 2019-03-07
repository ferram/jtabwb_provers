package ipl.frj.sequent;

import java.io.Serializable;
import java.util.HashMap;

import jtabwb.util.ImplementationError;
import jtabwbx.prop.basic.PropositionalConnective;
import jtabwbx.prop.formula.BitSetOfFormulas;
import jtabwbx.prop.formula.Formula;
import jtabwbx.prop.formula.FormulaFactory;
import jtabwbx.prop.formula.FormulaProposition;

/**
 * Formula factory for Nbu formulas.
 * 
 * @author Mauro Ferrari
 */
public class FrjFormulaFactory extends FormulaFactory implements Serializable {

  private HashMap<Integer, BitSetOfFormulas> subformulasOf; // to store already computed subformulas of a formula

  private static FrjFormulaFactory INSTANCE = null;

  public static FrjFormulaFactory getInstance() {
    if (INSTANCE == null)
      INSTANCE = new FrjFormulaFactory();
    return INSTANCE;
  }

  public static void resetInstance() {
    INSTANCE = null;
  }

  private FrjFormulaFactory() {
    super("false", "@TRUE"); // false is in the language, true is not
    super.setTranslateEquivalences(true); // IFF is not in the language
    super.setTranslateNegations(true); // NOT is not in the language
    super.setFormatImpliesFalseAsNegation(true);
    this.subformulasOf = new HashMap<Integer, BitSetOfFormulas>();
    
  }

  @Override
  public String getDescription() {
    return "Formula factory for Frj formulas";
  }

  /**
   * Returns the set of all sub-formulas of the specified formula.
   * 
   * @param wff
   * @return
   */
  public BitSetOfFormulas subFormulasOf(Formula wff) {
    BitSetOfFormulas bsf = subformulasOf.get(wff.getIndex());
    if (bsf != null)
      return bsf;
    bsf = new BitSetOfFormulas(this);
    bsf.add(wff);
    if (wff.isAtomic())
      return bsf;
    switch (wff.mainConnective()) {
    case OR:
    case IMPLIES:
    case EQ:
    case AND: {
      bsf.addAll(subFormulasOf(wff.immediateSubformulas()[0]));
      bsf.addAll(subFormulasOf(wff.immediateSubformulas()[1]));
      return bsf;
    }
    case NOT: {
      bsf.addAll(subFormulasOf(wff.immediateSubformulas()[0]));
      return bsf;
    }
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED);
    }
  }

  /**
   * Returns the new formula corresponding to the specified one. In general this
   * method is invoked to create with this factory a copy of the specified wff
   * (possibly built with another instance of the factory).
   * 
   * @param wff the formula to copy.
   * @return the copy of wff.
   */
  public Formula build(Formula wff) {
    if (wff.isAtomic())
      return this.buildAtomic(((FormulaProposition) wff).getName());

    Formula[] subwwfs = wff.immediateSubformulas();
    PropositionalConnective connective = wff.mainConnective();
    switch (connective) {
    case NOT:
      return buildCompound(connective, subwwfs[0]);
    case AND:
    case EQ:
    case IMPLIES:
    case OR:
      return buildCompound(connective, subwwfs);
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED);
    }
  }


}
