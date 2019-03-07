package ipl.frj.launcher;

import ipl.frj.sequent.FrjFormulaFactory;
import jtabwb.launcher.InitialGoalBuilderException;
import jtabwb.util.ImplementationError;
import jtabwbx.problems.JTabWbSimpleProblem;
import jtabwbx.problems.ProblemDescription;
import jtabwbx.prop.formula.Formula;
import jtabwbx.prop.parser.FormulaParseException;
import jtabwbx.prop.parser.PropositionalFormulaParser;

public class InitialGoalBuilder { //implements _InitialGoalBuilder {

  private FrjFormulaFactory formulaFactory;

  public InitialGoalBuilder() {
    FrjFormulaFactory.resetInstance();
    formulaFactory = FrjFormulaFactory.getInstance();
  };

  //@Override
  public Formula buildInitialNodeSet(ProblemDescription inputProblem)
      throws InitialGoalBuilderException {

    Formula goal = null;
    if (inputProblem instanceof JTabWbSimpleProblem) {
      goal = buildFrom((JTabWbSimpleProblem) inputProblem);
    } else
      throw new ImplementationError("Unkonw problem description.");

    return goal;
  }

  /**
   * Returns the formula corresponding to the given JTabWb-problem.
   * 
   * @param inputProblem the JTabWbProblem
   * @return the goal formula of the problem.
   * @throws InitialGoalBuilderException
   */
  private Formula buildFrom(JTabWbSimpleProblem inputProblem) throws InitialGoalBuilderException {

    // build the formula parser
    PropositionalFormulaParser parser = new PropositionalFormulaParser();

    String conjecture = inputProblem.getConjecture();
    if (conjecture == null)
      throw new InitialGoalBuilderException("No problem formula defined in the input problem.");

    Formula goal = null;
    try {
      goal = formulaFactory.buildFrom(parser.parse(conjecture));

    } catch (FormulaParseException e) {
      throw new InitialGoalBuilderException(e.getMessage());
    }
    return goal;
  }

  // private _FrjSequent buildFrom(ILTPProblem inputProblem) throws InitialGoalBuilderException {
  //
  // // build the formula parser
  // PropositionalFormulaParser parser = new PropositionalFormulaParser();
  //
  // // get the conjecture from the problem
  // String conjecture = inputProblem.getConjecture();
  // if (conjecture == null)
  // throw new InitialGoalBuilderException("No problem formula defined in the input problem.");
  //
  // // build formulas
  // Formula right = null;
  // LinkedList<Formula> left = null;
  // try {
  // // parse the conjecture and add it to the nodeset
  // right = formulaFactory.buildFrom(parser.parse(conjecture));
  //
  // // parse the axioms and add them to the nodeset
  // if (inputProblem.getAxioms() != null) {
  // left = new LinkedList<Formula>();
  // for (String axiom : inputProblem.getAxioms())
  // left.add(formulaFactory.buildFrom(parser.parse(axiom)));
  // }
  // } catch (FormulaParseException e) {
  // throw new InitialGoalBuilderException(e.getMessage());
  // }
  //
  // // build an empty UNBLOCKED SEQUENT
  // _NbuSequent nodeSet = getEmptyNodeSet(this.selectedImplementation, this.formulaFactory);
  // nodeSet.addRight(right);
  // if (left != null)
  // for (Formula axiom : left)
  // nodeSet.addLeft(axiom);
  // return nodeSet;
  // }

}
