package ipl.frj.latex;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileAlreadyExistsException;
import java.util.LinkedList;

import ferram.util.FileUtilis;
import ipl.frj.kripke.KripkeModel;
import ipl.frj.kripke.KripkeModelBuilder;
import ipl.frj.launcher.FrjSingleProblemExecutionData;
import ipl.frj.launcher.GbuSingleProblemExecutionData;
import ipl.frj.launcher.LauncherExecConfiguration;
import ipl.frj.sequent.FrjFormulaFactory;
import ipl.frj.tp.basic._FrjProver;
import ipl.frj.util.MSGManager;
import jtabwb.engine.ProofSearchResult;
import jtabwb.engine.Trace;
import jtabwb.tracesupport.LatexTranslator;
import jtabwb.util.ImplementationError;
import jtabwbx.problems.JTabWbSimpleProblem;

public class LatexGenerator {

  public static final String LATEX_FRJ_OUTPUT_DIR_PREFIX = "out-frj-";
  public static final String LATEX_FRJ_PROOF_FILE_NAME_PREFIX = "proof-frj-";
  public static final String LATEX_GBU_PROOF_FILE_NAME_PREFIX = "proof-gbu-";
  public static final String LATEX_PROOF_FILE_NAME_SUFFIX = ".tex";
  public static final String LATEX_FRJ_PROVED_SEQUENTS_PREFIX = "generated-sequents-frj-";
  public static final String LATEX_PROVED_SEQUENTS_SUFFIX = ".tex";
  public static final String GRAPHVIZ_MODEL_PREFIX = "model-frj-";
  public static final String GRAPHVIZ_MODEL_SUFFIX = ".gv";
  public static final String LATEX_MODEL_PREFIX = "model-frj-";
  public static final String LATEX_MODEL_SUFFIX = ".gv";
  public static final String MAKEFILE_FILE_NAME = "Makefile";

  public LatexGenerator(LauncherExecConfiguration launcherExecConf,
      FrjSingleProblemExecutionData frjExecData, GbuSingleProblemExecutionData gbuExecData)
      throws FileAlreadyExistsException {
    super();
    this.launcherExecConf = launcherExecConf;
    this.frjExecData = frjExecData;
    this.gbuExecData = gbuExecData;

    // build the name for output dir
    this.outputDir = new File(buildFileNameFrom(frjExecData.problemDescription().getProblemName(),
        LATEX_FRJ_OUTPUT_DIR_PREFIX, ""));

    // build the output dir, error if dir already exists
    if (this.outputDir.exists()) {
      if (!this.outputDir.isDirectory())
        throw new FileAlreadyExistsException(String.format(
            MSG.LATEX_GENERATOR.FILE_EXISTS_AND_IS_A_NOT_A_DIR, this.outputDir.getAbsolutePath()));
      else {
        FileUtilis.purgeDirectory(this.outputDir);
      }
    } else
      this.outputDir.mkdir();

    this.formulaFormatter =
        new FormulaFormatterWithAbbreviations((FrjFormulaFactory) frjExecData.goal().getFactory(),
            (JTabWbSimpleProblem) frjExecData.problemDescription(),
            frjExecData.getInitialNodeSetConstructionData().getGoal());
  }

  private FormulaFormatterWithAbbreviations formulaFormatter;
  private LauncherExecConfiguration launcherExecConf;
  private FrjSingleProblemExecutionData frjExecData;
  private GbuSingleProblemExecutionData gbuExecData;
  private File outputDir;

  // generates the latex file for the executed proof-search
  public void generateLatex() {
    _FrjProver frjProver = frjExecData.getProver();
    File frjGeneratedSequentsFile = null, frjProofFile = null, kripkeModelFile = null,
        gbuProofFile = null;
    switch (frjProver.getProofSearchDetails().getProofSearchResult()) {
    case FAILURE: {
      frjGeneratedSequentsFile = generate_frjLatexGeneratedSequents(frjExecData,
          (FrjFormulaFactory) frjExecData.goal().getFactory(), frjProver);
      if (launcherExecConf.execGbu())
        gbuProofFile = generate_gbuLatexProof(gbuExecData);
      break;
    }
    case SUCCESS: {
      frjProofFile = generate_frjLatexProof(frjExecData,
          (FrjFormulaFactory) frjExecData.goal().getFactory(), frjProver);
      frjGeneratedSequentsFile = generate_frjLatexGeneratedSequents(frjExecData,
          (FrjFormulaFactory) frjExecData.goal().getFactory(), frjProver);
      kripkeModelFile = generate_frjKripkeModel(frjExecData,
          (FrjFormulaFactory) frjExecData.goal().getFactory(), frjProver);
      break;
    }
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED);
    }
    generate_Makefile(frjGeneratedSequentsFile, frjProofFile, kripkeModelFile, gbuProofFile);
  }

  private File generate_gbuLatexProof(GbuSingleProblemExecutionData gbuExecData) {
    MSGManager.infoNoLn(MSG.INFO.GENERATE_GBU_LATEX_PROOF_BEGIN);
    String relativeFileName = buildFileNameFrom(gbuExecData.problemDescription().getProblemName(), //
        LATEX_GBU_PROOF_FILE_NAME_PREFIX, //
        LATEX_PROOF_FILE_NAME_SUFFIX);
    File outputFile = new File(this.outputDir, relativeFileName);
    PrintStream out = null;
    try {
      out = new PrintStream(outputFile);
      Trace trace = gbuExecData.getTrace();
      trace.pruneSuccessful();
      LatexTranslator translator =
          new LatexTranslator(trace, new GbuCTreeLatexFormatter(formulaFormatter));
      translator.generateLatex(out);
      MSGManager.info(MSG.INFO.GENERATE_GBU_LATEX_PROOF_END, outputFile.getAbsolutePath());
    } catch (IOException e) {
      MSGManager.error(MSG.ERROR.IO_EXCEPTION, e.getMessage());
      System.exit(1);
    } finally {
      if (out != null)
        out.close();
    }
    return outputFile;
  }

  private File generate_frjLatexGeneratedSequents(FrjSingleProblemExecutionData executionData,
      FrjFormulaFactory formulaFactory, _FrjProver prover) {
    MSGManager.infoNoLn(MSG.INFO.GENERATE_FRJ_LATEX_SEQUENTS_BEGIN);
    String relativeFileName = buildFileNameFrom(executionData.problemDescription().getProblemName(),
        LATEX_FRJ_PROVED_SEQUENTS_PREFIX, LATEX_PROVED_SEQUENTS_SUFFIX);
    File outputFile = new File(this.outputDir, relativeFileName);
    PrintStream out = null;
    try {
      out = new PrintStream(outputFile);
      FrjLatexSupport ls = new FrjLatexSupport(executionData, formulaFormatter, out);
      ls.generateLatexOfProvedSequents();
      MSGManager.info(MSG.INFO.GENERATE_FRJ_LATEX_SEQUENTS_END, outputFile.getAbsolutePath());
    } catch (IOException e) {
      MSGManager.error(MSG.ERROR.IO_EXCEPTION, e.getMessage());
      System.exit(1);
    } finally {
      if (out != null)
        out.close();
    }
    return outputFile;
  }

  private File generate_frjKripkeModel(FrjSingleProblemExecutionData executionData,
      FrjFormulaFactory formulaFactory, _FrjProver prover) {
    MSGManager.infoNoLn(MSG.INFO.GENERATE_FRJ_LATEX_MODEL_BEGIN);
    String modelFileName = buildFileNameFrom(executionData.problemDescription().getProblemName(),
        GRAPHVIZ_MODEL_PREFIX, GRAPHVIZ_MODEL_SUFFIX);
    File outputFile = new File(this.outputDir, modelFileName);
    PrintStream out = null;
    try {
      KripkeModel km = executionData.getKripkeModel();
      if (km == null) {
        KripkeModelBuilder builder =
            new KripkeModelBuilder(prover.getProofSearchDetails().getProvedSequentsDB());
        km = builder.build();
        executionData.setKripkeModel(km);
      }
      out = new PrintStream(outputFile);
      KripkeModelGraphvizGenerator latexGen =
          new KripkeModelGraphvizGenerator(km, executionData, modelFileName, out);
      latexGen.generateGraphviz();
      MSGManager.info(MSG.INFO.GENERATE_FRJ_LATEX_MODEL_END, outputFile.getAbsolutePath());
    } catch (IOException e) {
      MSGManager.error(MSG.ERROR.IO_EXCEPTION, e.getMessage());
      System.exit(1);
    } finally {
      if (out != null)
        out.close();
    }
    return outputFile;
  }

  private File generate_frjLatexProof(FrjSingleProblemExecutionData executionData,
      FrjFormulaFactory formulaFactory, _FrjProver prover) {
    MSGManager.infoNoLn(MSG.INFO.GENERATE_FRJ_LATEX_PROOF_BEGIN);
    String proofFileName = buildFileNameFrom(executionData.problemDescription().getProblemName(),
        LATEX_FRJ_PROOF_FILE_NAME_PREFIX, LATEX_PROOF_FILE_NAME_SUFFIX);
    File outputFile = new File(this.outputDir, proofFileName);

    if (executionData.proofSearchResult() == ProofSearchResult.SUCCESS) {
      PrintStream out = null;
      try {
        out = new PrintStream(outputFile);
        FrjLatexSupport ls = new FrjLatexSupport(executionData, formulaFormatter, out);
        ls.generateLatexOfProof();
        MSGManager.info(MSG.INFO.GENERATE_FRJ_LATEX_PROOF_END, outputFile.getAbsolutePath());
      } catch (IOException e) {
        MSGManager.error(MSG.ERROR.IO_EXCEPTION, e.getMessage());
        System.exit(1);
      } finally {
        if (out != null)
          out.close();
      }
      return outputFile;
    } else {
      MSGManager.info(MSG.INFO.GENERATE_FRJ_LATEX_PROOF_NOT_POSSIBLE);
      return null;
    }
  }

  static final String MAKEFILE_FRJ_GENERATED_VAR = "FRJ_GENERATED=$(basename %s)\n";
  static final String MAKEFILE_FRJ_PROOF_VAR = "FRJ_PROOF=$(basename %s)\n";
  static final String MAKEFILE_GBU_PROOF_VAR = "GBU_PROOF=$(basename %s)\n";
  static final String MAKEFILE_MODEL_VAR = "MODEL=$(basename %s)\n";

  static final String MAKEFILE_FRJ_GENERATED_TARGET = //
      "frj-generated:\n" //
          + "\t@echo -n -- Compiling LaTeX of generated sequents...\n" //
          + "\t@pdflatex -halt-on-error ${FRJ_GENERATED}.tex > /dev/null\n" //
          + "\t@echo \" output in [${FRJ_GENERATED}.pdf]\"\n\n";

  static final String MAKEFILE_FRJ_PROOF_TARGET = //
      "frj-proof:\n" //
          + "\t@echo -n -- Compiling LaTeX of generated proof...\n" //
          + "\t@pdflatex -halt-on-error ${FRJ_PROOF}.tex > /dev/null || (echo \"\\nERROR: [pdflatex ${FRJ_PROOF}.tex] FAILED \"; exit 1)\n" //
          + "\t@echo  \" output in [${FRJ_PROOF}.pdf] \"\n\n";

  static final String MAKEFILE_GBU_PROOF_TARGET = //
      "gbu-proof:\n" //
          + "\t@echo -n -- Compiling LaTeX of generated proof...\n" //
          + "\t@pdflatex -halt-on-error ${GBU_PROOF}.tex > /dev/null || (echo \"\\nERROR: [pdflatex ${GBU_PROOF}.tex] FAILED \"; exit 1)\n" //
          + "\t@echo  \" output in [${GBU_PROOF}.pdf] \"\n\n";

  static final String MAKEFILE_MODEL_TARGET = //
      "model:\n" // 
          + "\t@echo -n -- Generating .png of model\n" //
          + "\t@dot ${MODEL}.gv -Tpng -o ${MODEL}.png > /dev/null\n" //
          + "\t@echo \" output in [${MODEL}.png]\"\n\n";

  static final String MAKEFILE_CLEAN_TARGET = "clean:\n%s";

  static final String MAKEFILE_ALL_TARGET = "all: %s\n\n";

  static final String MAKEFILE_CLEAN_FRJ_GENERATED =
      "\trm -f ${FRJ_GENERTED}.{ps,pdf,log,aux,out,dvi,bbl,blg}\n";
  static final String MAKEFILE_CLEAN_FRJ_PROOF =
      "\trm -f ${FRJ_PROOF}.{ps,pdf,log,aux,out,dvi,bbl,blg}\n";
  static final String MAKEFILE_CLEAN_GBU_PROOF =
      "\trm -f ${GBU_PROOF}.{ps,pdf,log,aux,out,dvi,bbl,blg}\n";
  static final String MAKEFILE_CLEAN_MODEL = "\trm -f ${MODEL}.png\n"; //

  static final String MAKEFILE_TARGET_FRJ_GENERATED = "frj-generated";
  static final String MAKEFILE_TARGET_FRJ_PROOF = "frj-proof";
  static final String MAKEFILE_TARGET_GBU_PROOF = "gbu-proof";
  static final String MAKEFILE_TARGET_MODEL = "model";

  private void generate_Makefile(File frjGeneratedSequentsFile, File frjProofFile,
      File kripkeModelFile, File gbuProofFile) {

    MSGManager.infoNoLn(MSG.INFO.GENERATE_MAKEFILE_BEGIN);

    StringBuilder sbTarget = new StringBuilder();
    StringBuilder sbVar = new StringBuilder();
    StringBuilder sbClean = new StringBuilder();
    LinkedList<String> allTargetLabels = new LinkedList<String>();

    if (frjGeneratedSequentsFile != null) {
      sbVar.append(String.format(MAKEFILE_FRJ_GENERATED_VAR, frjGeneratedSequentsFile.getName()));
      sbTarget.append(MAKEFILE_FRJ_GENERATED_TARGET);
      sbClean.append(MAKEFILE_CLEAN_FRJ_GENERATED);
      allTargetLabels.add(MAKEFILE_TARGET_FRJ_GENERATED);
    }

    if (frjProofFile != null) {
      sbVar.append(String.format(MAKEFILE_FRJ_PROOF_VAR, frjProofFile.getName()));
      sbTarget.append(MAKEFILE_FRJ_PROOF_TARGET);
      sbClean.append(MAKEFILE_CLEAN_FRJ_PROOF);
      allTargetLabels.add(MAKEFILE_TARGET_FRJ_PROOF);
    }

    if (gbuProofFile != null) {
      sbVar.append(String.format(MAKEFILE_GBU_PROOF_VAR, gbuProofFile.getName()));
      sbTarget.append(MAKEFILE_GBU_PROOF_TARGET);
      sbClean.append(MAKEFILE_CLEAN_GBU_PROOF);
      allTargetLabels.add(MAKEFILE_TARGET_GBU_PROOF);
    }

    if (kripkeModelFile != null) {
      sbVar.append(String.format(MAKEFILE_MODEL_VAR, kripkeModelFile.getName()));
      sbTarget.append(MAKEFILE_MODEL_TARGET);
      sbClean.append(MAKEFILE_CLEAN_MODEL);
      allTargetLabels.add(MAKEFILE_TARGET_MODEL);
    }

    PrintStream out = null;
    try {
      File outfile = new File(outputDir, MAKEFILE_FILE_NAME);
      out = new PrintStream(outfile);
      // var dec
      out.println(sbVar.toString());
      // builds available targets string
      // print all target
      {
        String strTargets = "";
        String[] allts = allTargetLabels.toArray(new String[allTargetLabels.size()]);
        for (int i = 0; i < allts.length; i++)
          strTargets += allts[i] + (i < allts.length - 1 ? " " : "");
        out.print(String.format(MAKEFILE_ALL_TARGET, strTargets));
      }

      // other targets 
      out.println(sbTarget.toString());

      // clean target 
      out.println(String.format(MAKEFILE_CLEAN_TARGET, sbClean.toString()));

      MSGManager.info(MSG.INFO.GENERATE_MAKEFILE_END, outfile.getAbsolutePath());
      MSGManager.info(MSG.INFO.GENERATE_MAKEFILE_HINT);
    } catch (IOException e) {
      MSGManager.error(MSG.ERROR.IO_EXCEPTION, e.getMessage());
      System.exit(1);
    } finally {
      if (out != null)
        out.close();
    }
  }

  /*
   * Returns the string obtained replacing with spaces in name with underscore.
   */
  private String buildFileNameFrom(String problemName, String prefix, String suffix) {
    String strProblemName = problemName.trim().replaceAll("\\s", "_");
    return prefix + strProblemName + suffix;
  }

}
