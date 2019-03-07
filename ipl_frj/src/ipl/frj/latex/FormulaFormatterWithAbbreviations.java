package ipl.frj.latex;

import java.util.Collection;
import java.util.HashMap;

import ipl.frj.sequent.FrjFormulaFactory;
import jtabwbx.problems.JTabWbSimpleProblem;
import jtabwbx.problems.JTabWbSimpleProblem.Definition;
import jtabwbx.prop.basic.PropositionalConnective;
import jtabwbx.prop.formula.Formula;
import jtabwbx.prop.formula.FormulaFactory;
import jtabwbx.prop.formula.FormulaLatexFormatter;
import jtabwbx.prop.parser.FormulaParseException;
import jtabwbx.prop.parser.PropositionalFormulaParser;

public class FormulaFormatterWithAbbreviations {

  public FormulaFormatterWithAbbreviations(FrjFormulaFactory formulaFactory,
      JTabWbSimpleProblem problemDescription, Formula goal) {
    this.goal = goal;
    this.problemDescription = problemDescription;
    this.formulaFormatter = new FormulaLatexFormatter();

    // BUILDS ABBREVIATIONS
    // generate hash-maps for definitions
    if (problemDescription.getDefinitions() != null) {
      this.formulaFormatter.setFormulaAbbreviations(buildDefinitions(formulaFactory,problemDescription.getDefinitions()));
      this.applyAbbreviations = true;
    } else
      this.applyAbbreviations = false;

    formulaFormatter.setAbbreviateImpliesFalse(true);
    formulaFormatter.setCommandFor(PropositionalConnective.IMPLIES, "\\imp");
  }

  private Formula goal;
  private FormulaLatexFormatter formulaFormatter;
  private JTabWbSimpleProblem problemDescription;
  //private Definition[] formulaDefinitions;
  private HashMap<String, Formula> formulaDefinitionsByName;
  private HashMap<String, Formula> abbreviationsByName;
  private boolean applyAbbreviations = true;

  private HashMap<Formula, String> buildDefinitions(FrjFormulaFactory formulaFactory,
      Definition[] formulaDefinitions) {
    FormulaFactory abbreviationFactory = new FormulaFactory("false", "@TRUE"); // false is in the language, true is not
    abbreviationFactory.setTranslateEquivalences(true); // IFF is not in the language
    abbreviationFactory.setTranslateNegations(true); // NOT is not in the language
    abbreviationFactory.setFormatImpliesFalseAsNegation(true);

    HashMap<Formula, String> definitions = new HashMap<Formula, String>();
    this.formulaDefinitionsByName = new HashMap<String, Formula>();
    this.abbreviationsByName = new HashMap<String, Formula>();
    // build the formula parser
    PropositionalFormulaParser parser = new PropositionalFormulaParser();
    for (Definition def : formulaDefinitions) {
      try {
        // build the defined formula
        String expansion = def.getFormula();
        Formula defFormula = formulaFactory.buildFrom(parser.parse(expansion));
        definitions.put(defFormula, def.getName());
        this.formulaDefinitionsByName.put(def.getName(), defFormula);
        // build the abbreviation formula
        String abbreviation = def.getAbbreviation();
        Formula abbrFormuls = formulaFactory.buildFrom(parser.parse(abbreviation));
        abbreviationsByName.put(def.getName(), abbrFormuls);
      } catch (FormulaParseException e) {
        // TODO: sistemare gestione dell'eccezione
        throw new RuntimeException(e.getMessage());
      }
    }
    return definitions;
  }

  //  public Definition[] getFormulaDefinitions() {
  //    return problemDescription.getDefinitions();
  //  }

  public String getLatexOfFormulaByName(String name) {
    return formulaFormatter.toLatex(abbreviationsByName.get(name));
  }

  public String toLatex(Formula wff) {
    return formulaFormatter.toLatex(applyAbbreviations, wff);
  }

  public String toLatex(Collection<Formula> formulas, String separator) {
    return formulaFormatter.toLatex(applyAbbreviations, formulas, separator);
  }

  private final String GOAL_DETAILS_BEGIN = "\\section*{Goal details}\n\n" //
      + "\\begin{tabular}{ll}\n" + "Problem name: & %s\\\\\n" //
      + "Problem status: & %s\\\\\n" //
      + "Problem file name:& \\texttt{%s}\\\\\n" //
      + "\\end{tabular}\n\n";
  private final String GOAL_BEGIN = "\\begin{eqnarray*}\n";
  private final String GOAL_END = "\\end{eqnarray*}\n";
  private final String DEF_GOAL = "%s&=&%s\\\\[1ex]\n";
  private final String DEF_WFF_ABBR_ONLY = "%s&=&%s\\\\[1ex]\n";

  public String generateGoalAndDefinitionsDetails() {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format(GOAL_DETAILS_BEGIN //
        , correctName(problemDescription.getProblemName()) //
        , problemDescription.getProblemStatus().toString() //
        , correctName(problemDescription.getSource()) //
    ));
    sb.append("\n");
    // print goal and definitions
    sb.append(GOAL_BEGIN);
    if (problemDescription.getDefinitions() != null)
      for (Definition def : problemDescription.getDefinitions()) {
        sb.append(String.format(DEF_WFF_ABBR_ONLY, //
            def.getName(), getLatexOfFormulaByName(def.getName())));
      }
    sb.append(String.format(DEF_GOAL, "goal", formulaFormatter.toLatex(goal)));
    sb.append(GOAL_END);
    sb.append("\n");
    
    return sb.toString();
  }

  private static String[][] replacement = new String[][] { { "_", "\\\\_" }, //
    { "\\$", "\\$" } };

  private static String correctName(String name) {
    String result = name;
    for (int i = 0; i < replacement.length; i++)
      result = result.replaceAll(replacement[i][0], replacement[i][1]);
    return result;
  }
}
