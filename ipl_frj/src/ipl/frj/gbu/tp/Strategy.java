package ipl.frj.gbu.tp;

import java.util.Collection;

import ipl.frj.gbu.rules.Irreg_ClashDetectionRule;
import ipl.frj.gbu.rules.Irreg_Right_AND;
import ipl.frj.gbu.rules.Irreg_Right_IMPLIES_IN;
import ipl.frj.gbu.rules.Irreg_Right_IMPLIES_NOT_IN;
import ipl.frj.gbu.rules.Irreg_Right_OR;
import ipl.frj.gbu.rules.Reg_ClashDetectionRule;
import ipl.frj.gbu.rules.Reg_Left_AND;
import ipl.frj.gbu.rules.Reg_Left_IMPLIES;
import ipl.frj.gbu.rules.Reg_Left_OR;
import ipl.frj.gbu.rules.Reg_Right_AND;
import ipl.frj.gbu.rules.Reg_Right_IMPLIES_IN;
import ipl.frj.gbu.rules.Reg_Right_IMPLIES_NOT_IN;
import ipl.frj.gbu.rules.Reg_Right_OR;
import ipl.frj.gbu.sequent.GbuIrregularSequent;
import ipl.frj.gbu.sequent.GbuRegularSequent;
import ipl.frj.gbu.sequent._GbuSequent;
import ipl.frj.sequent.FrjFormulaFactory;
import ipl.frj.tp.basic.CandidateFormulas;
import ipl.frj.tp.basic._ProvedSequentsDB;
import jtabwb.engine.IterationInfo;
import jtabwb.engine._AbstractGoal;
import jtabwb.engine._AbstractRule;
import jtabwb.engine._Strategy;
import jtabwb.util.ImplementationError;
import jtabwbx.prop.basic.FormulaType;
import jtabwbx.prop.formula.Formula;

public class Strategy implements _Strategy {

  public Strategy(_ProvedSequentsDB db) {
    super();
    this.candidateFormulas = db.candidateFormulas();
    this.db = db;
    this.evaluator = new SaturatedDBEvaluator(this.db);
  }

  private final _ProvedSequentsDB db;
  private final CandidateFormulas candidateFormulas;
  private SaturatedDBEvaluator evaluator;

  @Override
  public _AbstractRule nextRule(_AbstractGoal currentGoal, IterationInfo lastIteration) {

    switch (((_GbuSequent) currentGoal).type()) {
    case REGULAR: {
      GbuRegularSequent goal = (GbuRegularSequent) currentGoal;

      // apply LEFT AND if possible 
      Formula mainFormula = goal.getLeft(FormulaType.AND_WFF);
      if (mainFormula != null)
        return new Reg_Left_AND(goal, mainFormula);

      // apply LEFT OR if possible 
      mainFormula = goal.getLeft(FormulaType.OR_WFF);
      if (mainFormula != null)
        return new Reg_Left_OR(goal, mainFormula);

      // get the right formula
      Formula right = goal.getRight();
      if (right.isAtomic()) {
        // apply left implies if possible
        Formula selectedImplication = selectLeftImplication(goal);
        if (selectedImplication != null)
          return new Reg_Left_IMPLIES(goal, selectedImplication);
      } else {

        // right formula is not atomic
        switch (right.mainConnective()) {
        case AND:
          return new Reg_Right_AND(goal, right);
        case IMPLIES: {
          Formula antecedent = right.immediateSubformulas()[0];
          if (candidateFormulas.isInClosure(antecedent, goal.leftSide()))
            return new Reg_Right_IMPLIES_IN(goal, right);
          else
            return new Reg_Right_IMPLIES_NOT_IN(goal, right);
        }
        case OR: {
          Formula selectedDisjunct = selectRightDisjunct(goal, right);
          // try RIGHT OR
          if (selectedDisjunct != null)
            return new Reg_Right_OR(goal, right, selectedDisjunct); // ottimizzare

          // try LEFT IMPLIES
          Formula selectedImplication = selectLeftImplication(goal);
          if (selectedImplication != null)
            return new Reg_Left_IMPLIES(goal, selectedImplication);

          break;
        }
        default:
          throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED);
        }
      }
      // try CLASH_DETECTION
      if (!(lastIteration.getAppliedRule() instanceof Reg_ClashDetectionRule))
        return new Reg_ClashDetectionRule(goal);

      throw new ImplementationError("Something went wrong sequent:\n" + goal.format());
    }
    case IRREGULAR: {
      GbuIrregularSequent goal = (GbuIrregularSequent) currentGoal;

      // try RIGHT IMPLIES
      Formula right = goal.getRight();

      // if right formula is atomic it can only be an axiom
      if (right.isAtomic()) {
        // try CLASH_DETECTION
        if (!(lastIteration.getAppliedRule() instanceof Irreg_ClashDetectionRule))
          return new Irreg_ClashDetectionRule(goal);
      }

      // if right formula is not atomic
      switch (right.mainConnective()) {
      case IMPLIES: {
        Formula antecedent = right.immediateSubformulas()[0];
        if (candidateFormulas.isInClosure(antecedent, goal.leftSide()))
          return new Irreg_Right_IMPLIES_IN(goal, right);
        else
          return new Irreg_Right_IMPLIES_NOT_IN(goal, right);
      }
      case AND:
        return new Irreg_Right_AND(goal, right);
      case OR: {
        Formula selectedDisjunct = selectRightDisjunct(goal, right);
        if (selectedDisjunct != null)
          return new Irreg_Right_OR(goal, right, selectedDisjunct);
        break;
      }
      default:
        throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED);
      }
    }

    default: // default for sequent type
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED);
    }
  }

  private Formula selectLeftImplication(GbuRegularSequent regular) {    
    Collection<Formula> leftImplications = regular.getAllLeftFormulas(FormulaType.IMPLIES_WFF);
    
    if (leftImplications == null)
      return null;

    for (Formula AimpliesB : leftImplications) {
      GbuIrregularSequent newSequent =
          new GbuIrregularSequent((FrjFormulaFactory) regular.getFormulaFactory());
      newSequent.addLeftAll(regular.leftSide());
      newSequent.addRight(AimpliesB.immediateSubformulas()[0]);
      if (!evaluator.isEvaluated(newSequent))
        return AimpliesB;
    }

    return null;
  }

  private Formula selectRightDisjunct(GbuIrregularSequent regular, Formula orRight) {
    for (int i = 0; i < 2; i++) {
      GbuIrregularSequent newIrregular = regular.clone();
      Formula selectedDisjunct = orRight.immediateSubformulas()[i];
      newIrregular.addRight(selectedDisjunct);
      if (!evaluator.isEvaluated(newIrregular))
        return selectedDisjunct;
    }

    return null;
  }

  private Formula selectRightDisjunct(GbuRegularSequent regular, Formula orRight) {
    for (int i = 0; i < 2; i++) {
      GbuIrregularSequent newIrregular =
          new GbuIrregularSequent((FrjFormulaFactory) regular.getFormulaFactory());
      newIrregular.addLeftAll(regular.leftSide().clone());
      Formula selectedDisjunct = orRight.immediateSubformulas()[i];
      newIrregular.addRight(selectedDisjunct);
      if (!evaluator.isEvaluated(newIrregular))
        return selectedDisjunct;
    }

    return null;
  }
}
