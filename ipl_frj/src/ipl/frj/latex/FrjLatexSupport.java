	package ipl.frj.latex;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;

import ipl.frj.launcher.FrjSingleProblemExecutionData;
import ipl.frj.rules.AbstractAx;
import ipl.frj.rules._FrjRule;
import ipl.frj.sequent.FrjIrregularSequent;
import ipl.frj.sequent.FrjRegularSequent;
import ipl.frj.sequent._FrjSequent;
import ipl.frj.tp.basic.FrjProofSearchDetails;
import ipl.frj.tp.basic._ProvedSequentsDB;
import ipl.frj.tp.basic._ProvedSequentsDBWithBwSubsumption;
import ipl.frj.tp.basic._SequentsTable;
import jtabwb.util.ImplementationError;
import jtabwbx.problems.JTabWbSimpleProblem.Definition;
import jtabwbx.problems.ProblemDescription;
import jtabwbx.prop.formula.BitSetOfFormulas;
import jtabwbx.prop.formula.Formula;

public class FrjLatexSupport {

  public class Abbreviation {

    String name;
    Formula formula;
    Formula abbreviation;
  }

  public FrjLatexSupport(FrjSingleProblemExecutionData executionData,
      FormulaFormatterWithAbbreviations formulaFormatter, PrintStream out) {
    super();
    this.executionData = executionData;
    this.formulaFormatter = formulaFormatter;
    this.out = out;
  }

  private FormulaFormatterWithAbbreviations formulaFormatter;
  private PrintStream out;
  private FrjSingleProblemExecutionData executionData;

  private final String LATEX_PREAMBLE_PROOF = //
      "\\documentclass[10pt]{article}\n" + //
          "\\usepackage{color}\n" + //
          "\\usepackage{xifthen}\n" + //
          "\\usepackage{xspace}\n" + //
          "\\usepackage[leqno]{amsmath}\n" + //
          "\\usepackage{amssymb}\n" + //
          "\\pdfpagewidth 200in\n" + //
          "\\pdfpageheight 100in\n" + //
          "\\DeclareMathSizes{10}{12}{12}{8}\n" + //
          "\\pagestyle{empty}" //
  ;

  private final String LATEX_PREAMBLE_GENERATED = //
      "\\documentclass[fleqn,10pt]{article}\n" + //
          "\\usepackage{color}\n" + //
          "\\usepackage{xifthen}\n" + //
          "\\usepackage{xspace}\n" + //
          "\\usepackage[leqno]{amsmath}\n" + //
          "\\usepackage{amssymb}\n" + //
          "\\usepackage[normalem]{ulem}\n" + //
          "\\usepackage{fancyhdr}\n " + //
          "%%\\pdfpagewidth 200in\n" + //
          "%%\\pdfpageheight 200in\n" + //
          "%%\\DeclareMathSizes{10}{12}{12}{8}\n" + //
          "\\pagestyle{empty}\n" + "\\setlength{\\mathindent}{0pt}\n\n\n"//
  ;

  private final String SEQ_MACRO = //
      "\\newcommand{\\imp}{\\supset}" + //
          "\\newcommand{\\emptySet}{\\cdot}" + //
          "\\usepackage{proof}\n" + //
          "\n" + //
          "% FRJ REGUALR\n" + //
          "\\newcommand{\\seqfrjArrow}{\\Rightarrow}\n" + //
          "\\newcommand{\\seqfrj}[2]{\n" + //
          "\\ifthenelse{\\isempty{#1}}{\\emptySet}{#1} % left\n" + // 
          "\\seqfrjArrow\n" + //
          "\\ifthenelse{\\isempty{#2}}{\\emptySet}{#2}\n" + // 
          "}\n" + //
          "\n" + //
          "% FRJ IRREGULAR\n" + //
          "\\newcommand{\\seqfrjiArrow}{\\rightarrow}\n" + //
          "\\newcommand{\\seqfrji}[3]{\n" + //
          "\\ifthenelse{\\isempty{#1}}{\\emptySet}{#1} % left 1\n" + //
          "\\,;\\,\n" + //
          "\\ifthenelse{\\isempty{#2}}{\\emptySet}{#2} % left 2\n" + //
          "\\seqfrjiArrow  % arrow \n" + //
          "\\ifthenelse{\\isempty{#3}}{\\emptySet}{#3} % right\n" + //
          "}\n";

  private final String LATEX_BEGIN_DOCUMENT = //
      "\\begin{document}\n" + //
          "\\thispagestyle{empty}\n"; //

  private final String LATEX_BEGIN_PROOF = "\\[\n";
  private final String LATEX_END_PROOF = "\\]\n\\vspace{4ex}";
  private final String LATEX_END_DOCUMENT = "\\end{document}";
  private final String LATEX_NEW_PAGE = "\\newpage";
  private final String GENERATED_ITERATION_LEGENDA = "\n\n\\noindent\n\\emph{Legenda}\\\\\n" + //
      "$\\bullet$: needed to prove the goal\\\\\n" + //
      "$sub(n)$: subsumed by sequent $n$\\\\\n\n";

  private final String GENERATED_HEADER = //
      "\\pagestyle{fancy}\n" + //
          "\\fancyhf{}\n" + //
          "\\rhead{Problem: %s}\n\n" //

  ;

  /**
   * Generates the LaTeX proof and write it on the specified stream.
   * 
   * @param out the stream where where the proof is written.
   */
  public void generateLatexOfProvedSequents() {
    out.print(LATEX_PREAMBLE_GENERATED);
    out.print(SEQ_MACRO);
    out.print(String.format(GENERATED_HEADER,
        correctName(executionData.problemDescription().getProblemName())));
    out.print(LATEX_BEGIN_DOCUMENT);
    // print goal and definitions
    out.print(formulaFormatter.generateGoalAndDefinitionsDetails());
    // print iterations
    out.print(GENERATED_ITERATION_LEGENDA);
    FrjProofSearchDetails psdetails = executionData.proofSearchDetails();
    _ProvedSequentsDB db = psdetails.getProvedSequentsDB();
    _ProvedSequentsDBWithBwSubsumption dbWithSubsumption = null;
    if (db instanceof _ProvedSequentsDBWithBwSubsumption)
      dbWithSubsumption = ((_ProvedSequentsDBWithBwSubsumption) db);
    HashSet<_FrjSequent> sequentsInProof = sequentsInProof();
    for (int iteration = 0; iteration <= psdetails.getNumberOfIterations(); iteration++)
      _generateIteration(db, iteration, dbWithSubsumption, sequentsInProof);
    out.print(LATEX_END_DOCUMENT);
  }

  private final String ITERATION_BEGIN = //
      "\\subsubsection*{Iteration %d}\n\n" + //
          "\\[\n\\begin{array}{llll}\n";
  private final String ITERATION_END = "\\end{array}\n\\]\n\n\n";
  private final String ITERATION_SEQUENT = "&(%d) &%s %s&%s\\\\[1.5ex]\n";
  private final String ITERATION_SEQUENT_SUBSUMED = "sub(%d)& (%d) & %s %s & %s\\\\[1.5ex]\n";
  private final String ITERATION_SEQUENT_INPROOF = "\\bullet&(%d) & %s %s & %s\\\\[1.5ex]\n";

  private void _generateIteration(_ProvedSequentsDB proved, int iteration,
      _ProvedSequentsDBWithBwSubsumption dbWithSubsumption, HashSet<_FrjSequent> sequentsInProof) {
    _SequentsTable iterationTable = proved.getIterationTable(iteration);
    Collection<FrjRegularSequent> regular = iterationTable.regular();
    Collection<FrjIrregularSequent> irregular = iterationTable.irregular();

    // build the array containing with all the proved sequents sorded by their progressive number 
    _FrjSequent[] allSequents = new _FrjSequent[(regular == null ? 0 : regular.size())
        + (irregular == null ? 0 : irregular.size())];
    {
      int j = 0;
      if (regular != null)
        for (_FrjSequent seq : regular)
          allSequents[j++] = seq;
      if (irregular != null)
        for (_FrjSequent seq : irregular)
          allSequents[j++] = seq;

      Arrays.sort(allSequents, new Comparator<_FrjSequent>() {

        @Override
        public int compare(_FrjSequent o1, _FrjSequent o2) {
          return o1.getSequentProgessiveNumber() - o2.getSequentProgessiveNumber();
        }

      });

    }

    // print all the sequents
    if (allSequents.length > 0) {
      out.print(String.format(ITERATION_BEGIN, iteration));
      for (_FrjSequent seq : allSequents) {
        _FrjRule rule = seq.generatingRule();
        _FrjSequent subsumingSeq = null;
        if (dbWithSubsumption != null)
          subsumingSeq = dbWithSubsumption.getFisrtSubsuming(seq);
        if (subsumingSeq == null) {
          boolean isSeqInProof = false;
          if (sequentsInProof != null && sequentsInProof.contains(seq))
            isSeqInProof = true;
          if (isSeqInProof)
            out.print(String.format(ITERATION_SEQUENT_INPROOF, seq.getSequentProgessiveNumber(),
                formatRuleName(rule), premisesIdxs(rule), formatSequent(seq)));
          else
            out.print(String.format(ITERATION_SEQUENT, seq.getSequentProgessiveNumber(),
                formatRuleName(rule), premisesIdxs(rule), formatSequent(seq)));
        } else
          out.print(String.format(ITERATION_SEQUENT_SUBSUMED,
              subsumingSeq.getSequentProgessiveNumber(), seq.getSequentProgessiveNumber(),
              formatRuleName(rule), premisesIdxs(rule), formatSequent(seq)));
      }
      out.print(ITERATION_END);
    }
  }

  private String premisesIdxs(_FrjRule rule) {
    _FrjSequent[] premises = rule.premises();
    String result = "";
    if (premises != null)
      for (_FrjSequent seq : premises)
        result += "(" + seq.getSequentProgessiveNumber() + ")";
    return result;
  }

//  private final String GOAL_DETAILS_BEGIN = "\\section*{Goal details}\n\n" //
//      + "\\begin{tabular}{ll}\n" + "Problem name: & %s\\\\\n" //
//      + "Problem status: & %s\\\\\n" //
//      + "Problem file name:& \\texttt{%s}\\\\\n" //
//      + "\\end{tabular}\n\n";
//  private final String GOAL_BEGIN = "\\begin{eqnarray*}\n";
//  private final String GOAL_END = "\\end{eqnarray*}\n";
//  private final String DEF_GOAL = "%s&=&%s\\\\[1ex]\n";
//  private final String DEF_WFF_ABBR_ONLY = "%s&=&%s\\\\[1ex]\n";

//  private void generateGoalDetails() {
//    ProblemDescription probelm = executionData.problemDescription();
//    out.println(String.format(GOAL_DETAILS_BEGIN //
//        , correctName(probelm.getProblemName()) //
//        , probelm.getProblemStatus().toString() //
//        , correctName(probelm.getSource()) //
//    ));
//    // print goal and definitions
//    out.print(GOAL_BEGIN);
//    if (formulaFormatter.getFormulaDefinitions() != null)
//      for (Definition def : formulaFormatter.getFormulaDefinitions()) {
//        out.print(String.format(DEF_WFF_ABBR_ONLY, //
//            def.getName(), formulaFormatter.getLatexOfFormulaByName(def.getName())));
//      }
//    out.print(
//        String.format(DEF_GOAL, "goal", formulaFormatter.toLatex(executionData.goal())));
//    out.print(GOAL_END);
//  }

  /**
   * Generates the LaTeX proof and write it on the specified stream.
   * 
   * @param out the stream where where the proof is written.
   */
  public void generateLatexOfProof() {
    out.print(LATEX_PREAMBLE_PROOF);
    out.print(SEQ_MACRO);
    out.print(LATEX_BEGIN_DOCUMENT);
    // print goal and definitions
    formulaFormatter.generateGoalAndDefinitionsDetails();
    // pint proof
    Collection<FrjRegularSequent> successSequents =
        executionData.proofSearchDetails().getSuccessSequents();
    FrjRegularSequent[] roots =
        successSequents.toArray(new FrjRegularSequent[successSequents.size()]);
    for (int i = 0; i < roots.length; i++) {
      out.print(LATEX_BEGIN_PROOF);
      _generateCTree(true, roots[i]);
      out.print(LATEX_END_PROOF);
      if (i < roots.length - 1)
        out.print(LATEX_NEW_PAGE);
    }
    out.print(LATEX_END_DOCUMENT);
  }

  private HashSet<_FrjSequent> sequentsInProof() {
    Collection<FrjRegularSequent> successSequents =
        executionData.proofSearchDetails().getSuccessSequents();
    if (successSequents == null)
      return null;
    FrjRegularSequent[] roots =
        successSequents.toArray(new FrjRegularSequent[successSequents.size()]);
    HashSet<_FrjSequent> setOfSequentsInTheProof = new HashSet<_FrjSequent>();
    addSequentsInProof(roots[0], setOfSequentsInTheProof);
    return setOfSequentsInTheProof;
  }

  private void addSequentsInProof(_FrjSequent root, HashSet<_FrjSequent> setOfSequentsInTheProof) {
    setOfSequentsInTheProof.add(root);
    _FrjRule rule = root.generatingRule();
    if (rule instanceof AbstractAx) {
      return;
    } else {
      _FrjSequent[] premises = rule.premises();
      for (int i = 0; i < premises.length; i++) {
        addSequentsInProof(premises[i], setOfSequentsInTheProof);
      }
    }
  }

  // format for inference rule \infer[rule_name]{prem}{cons1 & ... & consn}
  private final String INFER_FORMAT_1 = "\\infer[";
  private final String INFER_FORMAT_2 = "]{\n";
  private final String INFER_FORMAT_3 = "}{\n";
  private final String INFER_FORMAT_4 = "}\n";
  private final String INFER_FORMAT_AND = "&\n";

  int node_set_counter = 1;
  int rule_counter = 1;

  /* Generates a list of annotations to be printed after the c-tree */
  private void _generateCTree(boolean applyAbbreviations, _FrjSequent root) {
    _FrjRule rule = root.generatingRule();
    if (rule instanceof AbstractAx) {
      out.print(INFER_FORMAT_1);
      out.print(formatRuleName(rule));
      out.print(INFER_FORMAT_2);
      out.print(formatSequent(root));
      out.print(INFER_FORMAT_3);
      out.print(INFER_FORMAT_4);
    } else {
      out.print(INFER_FORMAT_1);
      out.print(formatRuleName(rule));
      out.print(INFER_FORMAT_2);
      out.print(formatSequent(root));
      out.print(INFER_FORMAT_3);
      _FrjSequent[] premises = rule.premises();
      for (int i = 0; i < premises.length; i++) {
        _generateCTree(applyAbbreviations, premises[i]);
        if (i < premises.length - 1)
          out.print(INFER_FORMAT_AND);
      }
      out.print(INFER_FORMAT_4);
    }
  }

  private final String REG_SEQ = "\\seqfrj{%s}{%s}";
  private final String IRR_SEQ = "\\seqfrji{%s}{%s}{%s}";

  private String formatSequent(_FrjSequent seq) {
    switch (seq.type()) {
    case REGULAR: {
      FrjRegularSequent rseq = (FrjRegularSequent) seq;
      BitSetOfFormulas leftSide = rseq.left();
      Collection<Formula> left = leftSide == null ? null : leftSide.getAllFormulas();
      String str_left = left == null ? "" : formulaFormatter.toLatex(left, ", ");
      String str_rigth =
          rseq.right() == null ? "" : formulaFormatter.toLatex(rseq.right());
      return String.format(REG_SEQ, str_left, str_rigth);
    }
    case IRREGULAR: {
      FrjIrregularSequent irrseq = (FrjIrregularSequent) seq;
      Collection<Formula> res = irrseq.stable() == null ? null : irrseq.stable().getAllFormulas();
      String str_res = res == null ? "" : formulaFormatter.toLatex(res, ", ");
      Collection<Formula> left =
          irrseq.nonStable() == null ? null : irrseq.nonStable().getAllFormulas();
      String str_left = left == null ? "" : formulaFormatter.toLatex(left, ", ");
      String str_rigth =
          irrseq.right() == null ? "" : formulaFormatter.toLatex(irrseq.right());
      return String.format(IRR_SEQ, str_res, str_left, str_rigth);
    }
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED);
    }
  }

  private String formatRuleName(_FrjRule rule) {
    switch (rule.getID()) {
    case AX_IRR:
      return "\\mathrm{Ax}_\\rightarrow";
    case AX_REG:
      return "\\mathrm{Ax}_\\Rightarrow";
    case AND_IRR:
      return "\\land";
    case AND_REG:
      return "\\land";
    case OR_IRR:
      return "\\lor";
    case IMP_IRR_IN_CL:
      return "\\imp_\\in";
    case IMP_REG_IN_CL:
      return "\\imp_\\in";
    case IMP_REG_NOT_IN_CL:
      return "\\imp_{\\not\\in}";
    case JOIN_ATOMIC:
      return "\\Join^{\\mathrm{At}}";
    case JOIN_DISJUNCTION:
      return "\\Join^\\lor";
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED);
    }
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
