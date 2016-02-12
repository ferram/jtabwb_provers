package s4.s4tab.tp;

import s4.s4tab.calculus.ClashDetectionRule;
import s4.s4tab.calculus.Rule_F_AND_branching;
import s4.s4tab.calculus.Rule_F_NOT;
import s4.s4tab.calculus.Rule_TC_AND;
import s4.s4tab.calculus.Rule_TC_NEC;
import s4.s4tab.calculus.Rule_TC_NOT_duplication;
import s4.s4tab.calculus.Rule_T_AND;
import s4.s4tab.calculus.Rule_T_NEC;
import s4.s4tab.calculus.Rule_T_NOT;
import s4.s4tab.nodeset.S4TabGoal;
import s4.s4tab.nodeset.Sign;
import jtabwb.engine.ForceBranchFailure;
import jtabwb.engine.IterationInfo;
import jtabwb.engine.IterationInfo.Move;
import jtabwb.engine.ProofSearchResult;
import jtabwb.engine._AbstractGoal;
import jtabwb.engine._AbstractRule;
import jtabwb.engine._Strategy;
import jtabwbx.modal.basic.ModalFormulaType;
import jtabwbx.modal.formula.BitSetOfModalFormulas;
import jtabwbx.modal.formula.ModalFormula;

/**
 * The S4Tab strategy.
 * 
 * @author Mauro Ferrari
 *
 */
class Strategy implements _Strategy {

  private LoopChecker loopChecker = new LoopChecker();
  private GlobalCache globalCache = new GlobalCache();

  @Override
  public _AbstractRule nextRule(_AbstractGoal currentGoal, IterationInfo lastIteration) {

    S4TabGoal goal = (S4TabGoal) currentGoal;

    ModalFormula selectedFormula;

    // Try regular non braching
    selectedFormula = goal.getFirstFormulaOfType(Sign.T, ModalFormulaType.AND_WFF);
    if (selectedFormula != null)
      return new Rule_T_AND(goal, selectedFormula);

    selectedFormula = goal.getFirstFormulaOfType(Sign.T, ModalFormulaType.NOT_WFF);
    if (selectedFormula != null)
      return new Rule_T_NOT(goal, selectedFormula);

    selectedFormula = goal.getFirstFormulaOfType(Sign.T, ModalFormulaType.BOX_WFF);
    if (selectedFormula != null)
      return new Rule_T_NEC(goal, selectedFormula);

    selectedFormula = goal.getFirstFormulaOfType(Sign.F, ModalFormulaType.NOT_WFF);
    if (selectedFormula != null)
      return new Rule_F_NOT(goal, selectedFormula);

    selectedFormula = goal.getFirstFormulaOfType(Sign.Tc, ModalFormulaType.AND_WFF);
    if (selectedFormula != null)
      return new Rule_TC_AND(goal, selectedFormula);

    selectedFormula = goal.getFirstFormulaOfType(Sign.Tc, ModalFormulaType.BOX_WFF);
    if (selectedFormula != null)
      return new Rule_TC_NEC(goal, selectedFormula);

    // Possible duplication
    BitSetOfModalFormulas TcNot = goal.getAllFormulasOfType(Sign.Tc, ModalFormulaType.NOT_WFF);
    for (ModalFormula wff : TcNot)
      if (!goal.getAlreadyTreatedDuplications().contains(wff)) {
        selectedFormula = wff;
        break;
      }
    if (selectedFormula != null)
      return new Rule_TC_NOT_duplication(goal, selectedFormula);

    // try regular branching
    selectedFormula = goal.getFirstFormulaOfType(Sign.F, ModalFormulaType.AND_WFF);
    if (selectedFormula != null)
      return new Rule_F_AND_branching(goal, selectedFormula);

    // check for clash
    if (lastIteration.getMove() != Move.CLASH_DETECTION_RULE_APPLICATION)
      return new ClashDetectionRule(goal);

    // check global cache
    ProofSearchResult alreadyKnown = globalCache.get(goal);
    if (alreadyKnown != null)
      if (alreadyKnown == ProofSearchResult.SUCCESS)
        return new RuleGlobalCacheSuccess(goal);
      else
        return new RuleGlobalCacheFailure(goal);

    // try not invertible F-NEC formulas
    if (loopChecker.contains(goal))
      return new ForceBranchFailure("Failure on loop check", goal);
    else {
      BitSetOfModalFormulas backtrackFormulas =
          goal.getAllFormulasOfType(Sign.F, ModalFormulaType.BOX_WFF);
      if (backtrackFormulas.cardinality() > 0)
        return new MetaBacktrackRule(globalCache, loopChecker, goal,
            backtrackFormulas.getAllFormulas());
    }

    return new ForceBranchFailure("No rule can be applied", goal);
  }

}
