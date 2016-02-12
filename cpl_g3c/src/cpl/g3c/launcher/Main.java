package cpl.g3c.launcher;

import cpl.g3c.tp.Prover;
import jtabwb.launcher.Launcher;
import jtabwbx.problems.ILTPProblemReader;
import jtabwbx.problems.JTabWbSimpleProblemReader;
import jtabwbx.problems.PlainProblemReader;

/**
 * Launcher for G3c prover.
 * @author Mauro Ferrari
 *
 */
public class Main {

  private static String FORMULA_SYNTAX_DESCRIPTION = //
  "Syntax of formulas\n" + "  atoms: Java identifiers\n" + //
      "logical: false, & (and), | (or), ~ (not), -> (implies), <=> (iff)\n" + //
      "  notes: (~ A) is translated as (A -> false)\n" + //
      "         (A <=> B) is translated as ((A -> B) & (B -> A))";

  private Launcher launcher;

  private Main() {
    super();
    this.launcher = new Launcher();
  }

  private void start(String[] args) {
    configureLauncher();
    launcher.processCmdLineArguments(args);
    launcher.launch();
  }

  private void configureLauncher() {
    launcher.configTheoremProver("jpcltp", Prover.class, true);
    launcher.configProblemDescriptionReader("plain", PlainProblemReader.class, false);
    launcher.configProblemDescriptionReader("pitp", JTabWbSimpleProblemReader.class, true);
    launcher.configProblemDescriptionReader("iltp", ILTPProblemReader.class, false);

    InitialNodeSetBuilder i = new InitialNodeSetBuilder();
    launcher.configInitialNodeSetBuilder(i);
    launcher.optConfigSingleExecutionConfigurator(new SingelExecutionConfigurator(i));
    launcher.configLauncherName(this.getClass().getCanonicalName());
    launcher
        .optConfigWelcomeMessage("jpcltp - Prover for Classical propositional logic based on G3, ver. 1.0");
    launcher.configStandardInputReader(new PlainProblemReader());
    launcher.optConfigInputSyntax(FORMULA_SYNTAX_DESCRIPTION);
  }

  public static void main(String[] args) {
    Main main = new Main();
    main.start(args);
  }

}
