package cpl.g3c.tp;

import java.util.EnumSet;

import cpl.g3c.calculus.ClashDetectionRule;
import cpl.g3c.calculus.Rule_Left_AND;
import cpl.g3c.calculus.Rule_Left_IMPLIES;
import cpl.g3c.calculus.Rule_Left_NOT;
import cpl.g3c.calculus.Rule_Left_OR;
import cpl.g3c.calculus.Rule_Right_AND;
import cpl.g3c.calculus.Rule_Right_IMPLIES;
import cpl.g3c.calculus.Rule_Right_NOT;
import cpl.g3c.calculus.Rule_Right_OR;
import jtabwb.engine.IterationInfo;
import jtabwb.engine._AbstractGoal;
import jtabwb.engine._AbstractRule;
import jtabwb.engine._Strategy;
import jtabwb.util.CaseNotImplementedImplementationError;
import jtabwbx.prop.basic.FormulaType;
import jtabwbx.prop.basic.PropositionalConnective;
import jtabwbx.prop.formula.Formula;
import jtabwbx.prop.formula._Sequent;

/**
 * Strategy for G3c
 * @author Mauro Ferrari
 *
 */
class Strategy implements _Strategy {

  @Override
  public _AbstractRule nextRule(_AbstractGoal currentNode, IterationInfo lastIteration) {
    _Sequent premise = (_Sequent) currentNode;
    Formula mainFormula;

    mainFormula = getLeftCompound(premise);
    if (mainFormula != null)
      return getLeftRuleFor(premise, mainFormula);

    mainFormula = getRightCompound(premise);
    if (mainFormula != null)
      return getRighRuleFor(premise, mainFormula);

    if (lastIteration.getMove() != IterationInfo.Move.CLASH_DETECTION_RULE_APPLICATION)
      return new ClashDetectionRule(premise);

    return null;
  }

  private final EnumSet<FormulaType> typeOfCompound = EnumSet.of(FormulaType.AND_WFF,
      FormulaType.OR_WFF, FormulaType.IMPLIES_WFF, FormulaType.NOT_WFF);

  /**
   * Returns a compound formula from the right-hand side of the sequent or
   * <code>null</code> if the sequent does not contain compound formulas in the
   * right hand side.
   * 
   * @return a compond formula from the right hand side of the sequent.
   */
  private Formula getRightCompound(_Sequent sequent) {
    for (FormulaType type : typeOfCompound) {
      Formula result = null;
      if ((result = sequent.getRight(type)) != null)
        return result;
    }
    return null;
  }

  /**
   * Returns a compound formula from the left-hand side of the sequent or
   * <code>null</code> if the sequent does not contain compound formulas in the
   * left-hand side.
   * 
   * @return a compond formula from the left-hand side of the sequent.
   */
  private Formula getLeftCompound(_Sequent sequent) {
    for (FormulaType type : typeOfCompound) {
      Formula result = null;
      if ((result = sequent.getLeft(type)) != null)
        return result;
    }
    return null;
  }

  private _AbstractRule getLeftRuleFor(_Sequent premise, Formula mainFormula) {
    PropositionalConnective connective = mainFormula.mainConnective();
    switch (connective) {
    case AND:
      return new Rule_Left_AND(premise, mainFormula);
    case OR:
      return new Rule_Left_OR(premise, mainFormula);
    case IMPLIES:
      return new Rule_Left_IMPLIES(premise, mainFormula);
    case NOT:
      return new Rule_Left_NOT(premise, mainFormula);
    default:
      throw new CaseNotImplementedImplementationError(connective.name());
    }
  }

  private _AbstractRule getRighRuleFor(_Sequent premise, Formula mainFormula) {
    PropositionalConnective connective = mainFormula.mainConnective();
    switch (connective) {
    case AND:
      return new Rule_Right_AND(premise, mainFormula);
    case OR:
      return new Rule_Right_OR(premise, mainFormula);
    case IMPLIES:
      return new Rule_Right_IMPLIES(premise, mainFormula);
    case NOT:
      return new Rule_Right_NOT(premise, mainFormula);
    default:
      throw new CaseNotImplementedImplementationError(connective.getName());
    }
  }

}
