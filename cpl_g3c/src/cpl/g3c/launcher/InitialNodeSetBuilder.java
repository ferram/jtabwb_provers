package cpl.g3c.launcher;

import java.util.LinkedList;

import jtabwb.launcher.InitialGoalBuilderException;
import jtabwb.launcher._InitialGoalBuilder;
import jtabwb.util.ImplementationError;
import jtabwbx.problems.ILTPProblem;
import jtabwbx.problems.JTabWbSimpleProblem;
import jtabwbx.problems.ProblemDescription;
import jtabwbx.prop.formula.Formula;
import jtabwbx.prop.formula.FormulaFactory;
import jtabwbx.prop.formula.SequentOnArray;
import jtabwbx.prop.formula._Sequent;
import jtabwbx.prop.parser.FormulaParseException;
import jtabwbx.prop.parser.PropositionalFormulaParser;

class InitialNodeSetBuilder implements _InitialGoalBuilder {

  private FormulaFactory formulaFactory;

  public InitialNodeSetBuilder() {
  }

  public void setFormulaFactory(FormulaFactory formulaFactory) {
    this.formulaFactory = formulaFactory;
  }

  @Override
  public _Sequent buildInitialNodeSet(ProblemDescription inputProblem)
      throws InitialGoalBuilderException {

    if (inputProblem instanceof JTabWbSimpleProblem) {
      JTabWbSimpleProblem pd = (JTabWbSimpleProblem) inputProblem;
      // get the conjecture
      String problem = pd.getConjecture();
      if (pd.getConjecture() == null) {
        throw new InitialGoalBuilderException("No problem formula defined in the input problem.");
      }
      
      // build the conjecture formula
      PropositionalFormulaParser parser = new PropositionalFormulaParser();
      Formula wff = null;
      try {
        wff = formulaFactory.buildFrom(parser.parse(problem));
      } catch (FormulaParseException e) {
        throw new InitialGoalBuilderException(e.getMessage());
      }

      // build the mode set
      SequentOnArray sequent = new SequentOnArray(this.formulaFactory);
      sequent.addRight(wff);

      return sequent;
    }

    if (inputProblem instanceof ILTPProblem) {
      ILTPProblem pd= (ILTPProblem) inputProblem;
      // get the conjecture
      String problem = pd.getConjecture();
      if (pd.getConjecture() == null) {
        throw new InitialGoalBuilderException("No problem formula defined in the input problem.");
      }
      
      // build the conjecture and axiom formulas
      PropositionalFormulaParser parser = new PropositionalFormulaParser();
      Formula right = null;
      LinkedList<Formula> axioms = null;
      try {
        right = formulaFactory.buildFrom(parser.parse(problem));
        
        if (pd.getAxioms() != null){
          axioms = new LinkedList<Formula>();
          for (String axiom: pd.getAxioms())
            axioms.add(formulaFactory.buildFrom(parser.parse(axiom)));
        }
      } catch (FormulaParseException e) {
        throw new InitialGoalBuilderException(e.getMessage());
      }

      SequentOnArray sequent = new SequentOnArray(this.formulaFactory);
      if (axioms != null)
        for (Formula wff: axioms)
          sequent.addLeft(wff);
      
      sequent.addRight(right);
      return sequent;
    }

    throw new ImplementationError("Unkonw problem description.");

  }

  public FormulaFactory getFormulaFactory() {
    return formulaFactory;
  }

}
