package ipl.frj.latex;

import ipl.frj.gbu.rules._GbuRule;
import ipl.frj.gbu.sequent._GbuSequent;
import jtabwb.engine._AbstractGoal;
import jtabwb.engine._AbstractRule;
import jtabwb.tracesupport.CTree;
import jtabwb.tracesupport.LatexTranslator.ProofStyle;
import jtabwbx.prop.basic.PropositionalConnective;
import jtabwbx.prop.formula.FormulaLatexFormatter;
import jtabwb.tracesupport._LatexCTreeFormatter;

/**
 * Implementation of {@link _LatexCTreeFormatter} for the FRJ-GBU calculus.
 * 
 * @author Mauro Ferrari
 */
public class GbuCTreeLatexFormatter implements _LatexCTreeFormatter {

  private static final String SEQUENTE_LATEX_MACROS = //
      "% CONNECTIVES\n" // 
          + "\\newcommand{\\emptySet}{\\cdot}"  //
          + "\\usepackage{proof}\n"  //
          + "\n"  //
          + "\\newcommand{\\imp}{\\supset}" //
          + "% GBU REGUALAR SEQUENT\n" // 
          + "\\newcommand{\\seqgbuArrow}{\\Rightarrow_g}\n" //
          + "\\newcommand{\\seqgbu}[2]{\n" //
          + "\\ifthenelse{\\isempty{#1}}{\\emptySet}{#1 " + "} % left\n" // 
          + "\\seqgbuArrow\n" //
          + "\\ifthenelse{\\isempty{#2}}{\\emptySet}{#2} % right\n" //
          + "}" //
          + "\n\n" //
          + "% GBU IRREGULAR SEQUENT\n" //
          + "\\newcommand{\\seqgbuiArrow}{\\rightarrow_g}\n" //
          + "\\newcommand{\\seqgbui}[2]{ " //
          + "\\ifthenelse{\\isempty{#1}}{\\emptySet}{#1} % left\n" //  
          + "\\seqgbuiArrow\n" //
          + "\\ifthenelse{\\isempty{#2}}{\\emptySet}{#2}\n" // 
          + "}" //
          + "\n\n";

  private FormulaFormatterWithAbbreviations formulaFormatter;

  public GbuCTreeLatexFormatter(FormulaFormatterWithAbbreviations formulaFormatter) {
    super();
    this.formulaFormatter = formulaFormatter;
  }

  @Override
  public String getPreamble() {
    return SEQUENTE_LATEX_MACROS;
  }

  @Override
  public String getIntro() {
    return formulaFormatter.generateGoalAndDefinitionsDetails();
  }

  @Override
  public ProofStyle proofStyle() {
    return ProofStyle.SEQUENT;
  }

  @Override
  public boolean generateFailureGoalAnnotations() {
    return false;
  }

  public String format(_AbstractGoal nodeSet) {
    return toLatex((_GbuSequent) nodeSet);
  }

  /*
   * @see
   * jptp.util._LatexProofFormatter#formatRuleName(jptp.basic._AbstractRule)
   */
  public String formatRuleName(_AbstractRule rule) {
    _GbuRule r = (_GbuRule) rule;
    switch (r.getRuleIdentifier()) {
    case IRREG_CLASH_DETECTION:
    case REG_CLASH_DETECTION:
      return "\\textrm{Id}";
    case REG_LEFT_AND:
      return "\\land L";
    case REG_LEFT_OR:
      return "\\lor L";
    case REG_LEFT_IMPLIES:
      return "\\to L";
    case IRREG_RIGHT_AND:
    case REG_RIGHT_AND:
      return "\\land R";
    case IRREG_RIGHT_OR:
    case REG_RIGHT_OR:
      return "\\lor R";
    case IRREG_RIGHT_IMPLIES_IN:
    case REG_RIGHT_IMPLIES_IN:
      return "\\imp R_{\\in}";
    case IRREG_RIGHT_IMPLIES_NOT_IN:
    case REG_RIGHT_IMPLIES_NOT_IN:
      return "\\imp R_{\\not\\in}";
    default:
      return null;
    }

  }

  @Override
  public boolean generateNodeSetIndex() {
    return true;
  }

  @Override
  public boolean generateRuleIndex() {
    return false;
  }

  @Override
  public String pre(CTree ctree) {
    return null;
  }

  @Override
  public String post(CTree ctree) {
    return null;
  }

  final static String REG_SEQ_FORMAT = "\\seqgbu{%s}{%s}";
  final static String IRREG_SEQ_FORMAT = "\\seqgbui{%s}{%s}";

  public String toLatex(_GbuSequent seq) {
    String format = null;
    if (seq.isRegular())
      format = REG_SEQ_FORMAT;
    else
      format = IRREG_SEQ_FORMAT;

    return String.format(format, formulaFormatter.toLatex(seq.getAllLeftFormulas(), ", "),
        formulaFormatter.toLatex(seq.getRight()));
  }

}
