package s4.s4tab.nodeset;

import java.util.Collection;

import jtabwb.util.CaseNotImplementedImplementationError;
import jtabwb.util.ImplementationError;
import jtabwbx.modal.basic.ModalConnective;
import jtabwbx.modal.basic.ModalFormulaType;
import jtabwbx.modal.formula.BitSetOfModalFormulas;
import jtabwbx.modal.formula.ModalFormula;

/**
 * Models a node set of signed formulas, the set only treats formulas over the language: NOT, AND, BOX.
 * 
 * @author Mauro Ferrari
 *
 */
public class S4TabGoal implements _S4TabGoal, Cloneable {

  private final String FMT_TT = "T[%s]";
  private final String FMT_FF = "F[%s]";
  private final String FMT_TC = "Tc[%s]";

  BitSetOfModalFormulas tt_regular; // T-AND, T-NOT, T-NEC
  BitSetOfModalFormulas ff_regular; // FF-AND, FF-NOT
  BitSetOfModalFormulas ff_noninv; // F-NEC
  BitSetOfModalFormulas tc_regular; // Tc-AND, Tc-NEC
  BitSetOfModalFormulas tc_duplication; // Tc-NOT-AND Tc-NOT-NEC
  BitSetOfModalFormulas tc_duplication_already_treated;

  private TabS4FormulaFactory formulaFactory;
  private ModalFormula FALSE;
  private ModalFormula TRUE;

  public S4TabGoal(TabS4FormulaFactory factory) {
    this.formulaFactory = factory;
    this.FALSE = factory.getFalse();
    this.TRUE = factory.getTrue();
    this.tt_regular = new BitSetOfModalFormulas(factory);
    this.ff_regular = new BitSetOfModalFormulas(factory);
    this.ff_noninv = new BitSetOfModalFormulas(factory);
    this.tc_regular = new BitSetOfModalFormulas(factory);
    this.tc_duplication = new BitSetOfModalFormulas(factory);
    this.tc_duplication_already_treated = new BitSetOfModalFormulas(factory);
  }

  private S4TabGoal() {
  }

  public void addAlreadyTreatedTcDuplication(ModalFormula wff) {
    tc_duplication_already_treated.add(wff);
  }

  public BitSetOfModalFormulas getAlreadyTreatedDuplications() {
    return tc_duplication_already_treated;
  }

  @Override
  public void addSigned(Sign sign, ModalFormula wff) {
    switch (sign) {
    case T:
      tt_regular.add(wff);
      break;
    case F:
      if (wff.isCompound() && wff.mainConnective() == ModalConnective.BOX)
        ff_noninv.add(wff);
      else
        ff_regular.add(wff);
      break;
    case Tc:
      if (wff.isCompound() && (wff.mainConnective() == ModalConnective.NOT)) {
        //        ModalFormula subformula = wff.immediateSubformulas()[0];
        //        if (subformula.isAtomic() || subformula.mainConnective() == ModalConnective.NOT)
        //          tc_regular.add(wff);
        //        else
        tc_duplication.add(wff);
      } else
        tc_regular.add(wff);
      break;
    default:
      throw new CaseNotImplementedImplementationError(sign.name());
    }
  }

  @Override
  public boolean checkForClash() {
    return tt_regular.intersects(ff_regular) //
        || tt_regular.intersects(ff_noninv) //
        || tc_regular.intersects(ff_regular) //
        || tc_regular.intersects(ff_noninv) //
        || tc_duplication.intersects(ff_regular) //
        || tt_regular.contains(FALSE) //
        || tc_regular.contains(FALSE) //
        || ff_regular.contains(TRUE) //
        || tc_regular.contains(FALSE);
  }

  @Override
  public boolean contains(Sign sign, ModalFormula wff) {
    switch (sign) {
    case T:
      return tt_regular.contains(wff);
    case F:
      return ff_regular.contains(wff) || ff_noninv.contains(wff);
    case Tc:
      return tc_regular.contains(wff) || tc_duplication.contains(wff)
          || tc_duplication_already_treated.contains(wff);
    default:
      throw new CaseNotImplementedImplementationError(sign.name());
    }
  }

  @Override
  public TabS4FormulaFactory getFormulaFactory() {
    return formulaFactory;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.ff_noninv == null) ? 0 : this.ff_noninv.hashCode());
    result = prime * result + ((this.ff_regular == null) ? 0 : this.ff_regular.hashCode());
    result = prime * result + ((this.tc_duplication == null) ? 0 : this.tc_duplication.hashCode());
    result = prime * result + ((this.tc_regular == null) ? 0 : this.tc_regular.hashCode());
    result = prime * result + ((this.tt_regular == null) ? 0 : this.tt_regular.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    S4TabGoal other = (S4TabGoal) obj;
    if (this.ff_noninv == null) {
      if (other.ff_noninv != null)
        return false;
    } else if (!this.ff_noninv.equals(other.ff_noninv))
      return false;
    if (this.ff_regular == null) {
      if (other.ff_regular != null)
        return false;
    } else if (!this.ff_regular.equals(other.ff_regular))
      return false;
    if (this.tc_duplication == null) {
      if (other.tc_duplication != null)
        return false;
    } else if (!this.tc_duplication.equals(other.tc_duplication))
      return false;
    if (this.tc_regular == null) {
      if (other.tc_regular != null)
        return false;
    } else if (!this.tc_regular.equals(other.tc_regular))
      return false;
    if (this.tt_regular == null) {
      if (other.tt_regular != null)
        return false;
    } else if (!this.tt_regular.equals(other.tt_regular))
      return false;
    return true;
  }

  @Override
  public BitSetOfModalFormulas getAllFormulasOfType(Sign sign, ModalFormulaType formulaType) {
    switch (sign) {
    case T:
      return tt_regular.getBitsetOfAllFormulas(formulaType);
    case F:
      if (formulaType == ModalFormulaType.BOX_WFF)
        return ff_noninv;
      else
        return ff_regular.getBitsetOfAllFormulas(formulaType);
    case Tc:
      if (formulaType == ModalFormulaType.NOT_WFF)
        return tc_duplication;
      else
        return tc_regular.getBitsetOfAllFormulas(formulaType);
    default:
      throw new CaseNotImplementedImplementationError(sign.name());
    }
  }

  @Override
  public ModalFormula getFirstFormulaOfType(Sign sign, ModalFormulaType formulaType) {
    switch (sign) {
    case T:
      return tt_regular.getFirst(formulaType);
    case F:
      if (formulaType == ModalFormulaType.BOX_WFF)
        return ff_noninv.getFirst();
      else
        return ff_regular.getFirst(formulaType);
    case Tc:
      if (formulaType == ModalFormulaType.NOT_WFF)
        return tc_duplication.getFirst();
      else
        return tc_regular.getFirst(formulaType);
    default:
      throw new CaseNotImplementedImplementationError(sign.name());
    }
  }

  @Override
  public boolean removeSigned(Sign sign, ModalFormula wff) {
    switch (sign) {
    case T:
      return tt_regular.remove(wff);
    case F:
      if (wff.mainConnective() == ModalConnective.BOX)
        return ff_noninv.remove(wff);
      else
        return ff_regular.remove(wff);
    case Tc:
      if (wff.mainConnective() == ModalConnective.NOT)
        return tc_duplication.remove(wff);
      else
        return tc_regular.remove(wff);
    default:
      throw new CaseNotImplementedImplementationError(sign.name());
    }
  }

  @Override
  public String format() {
    return this.toString();
  }

  @Override
  public S4TabGoal clone() {
    try {
      S4TabGoal cloned = (S4TabGoal) super.clone();
      cloned.formulaFactory = this.formulaFactory;
      cloned.FALSE = this.FALSE;
      cloned.TRUE = this.TRUE;
      cloned.tt_regular = this.tt_regular.clone();
      cloned.ff_regular = this.ff_regular.clone();
      cloned.ff_noninv = this.ff_noninv.clone();
      cloned.tc_regular = this.tc_regular.clone();
      cloned.tc_duplication = this.tc_duplication.clone();
      return cloned;
    } catch (CloneNotSupportedException e) {
      throw new ImplementationError(e.getMessage());
    }
  }

  @Override
  public S4TabGoal cloneStablePart() {
    S4TabGoal cloned = new S4TabGoal();
    cloned.formulaFactory = this.formulaFactory;
    cloned.FALSE = this.FALSE;
    cloned.TRUE = this.TRUE;
    cloned.tt_regular = new BitSetOfModalFormulas(this.formulaFactory);
    cloned.ff_regular = new BitSetOfModalFormulas(this.formulaFactory);
    cloned.ff_noninv = new BitSetOfModalFormulas(this.formulaFactory);
    cloned.tc_regular = this.tc_regular.clone();
    cloned.tc_duplication = this.tc_duplication.clone();
    cloned.tc_duplication_already_treated = new BitSetOfModalFormulas(this.formulaFactory);
    return cloned;
  }

  public S4TabGoal cloneTcPart() {
    S4TabGoal cloned = new S4TabGoal();
    cloned.formulaFactory = this.formulaFactory;
    cloned.FALSE = this.FALSE;
    cloned.TRUE = this.TRUE;
    cloned.tt_regular = new BitSetOfModalFormulas(this.formulaFactory);
    cloned.ff_regular = new BitSetOfModalFormulas(this.formulaFactory);
    cloned.ff_noninv = new BitSetOfModalFormulas(this.formulaFactory);
    cloned.tc_regular = this.tc_regular.clone();
    cloned.tc_duplication = this.tc_duplication.clone();
    cloned.tc_duplication_already_treated = new BitSetOfModalFormulas(this.formulaFactory);
    return cloned;
  }

  @Override
  public String toString() {
    String[] astr = { //
            toString(tt_regular.getAllFormulas(), FMT_TT), //
            toString(ff_regular.getAllFormulas(), FMT_FF), //
            toString(ff_noninv.getAllFormulas(), FMT_FF),
            toString(tc_regular.getAllFormulas(), FMT_TC), //
            toString(tc_duplication.getAllFormulas(), FMT_TC) //
        };

    String str = "";
    for (int i = 0; i < astr.length; i++)
      str +=
          astr[i] == null ? "" : astr[i]
              + ((i < astr.length - 1) && astr[i + 1] != null ? ", " : "");

    return str;
  }

  private String toString(Collection<ModalFormula> coll, String FMT) {
    if (coll == null)
      return null;
    ModalFormula[] a = coll.toArray(new ModalFormula[coll.size()]);
    String str = "";
    for (int i = 0; i < a.length; i++)
      str += String.format(FMT, a[i].toString()) + (i < a.length - 1 ? ", " : "");
    return str;
  }

}
