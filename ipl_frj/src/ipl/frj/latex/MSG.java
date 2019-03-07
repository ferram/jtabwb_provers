package ipl.frj.latex;

public class MSG {

  static class ERROR {

    static final String IO_EXCEPTION = "IO EXCEPTION... %s";
  }

  static class INFO {

    static final String GENERATE_FRJ_LATEX_PROOF_BEGIN = "Generating frj proof... ";
    static final String GENERATE_FRJ_LATEX_PROOF_END = "saved in [%s]";
    static final String GENERATE_FRJ_LATEX_PROOF_NOT_POSSIBLE =
        "Latex of frj proof can be performed only for a successful proof-search";

    static final String GENERATE_GBU_LATEX_PROOF_BEGIN = "Generating gbu proof... ";
    static final String GENERATE_GBU_LATEX_PROOF_END = "saved in [%s]";

    static final String GENERATE_FRJ_LATEX_SEQUENTS_BEGIN = "Generating sequents... ";
    static final String GENERATE_FRJ_LATEX_SEQUENTS_END = "saved in [%s]";

    static final String GENERATE_FRJ_LATEX_MODEL_BEGIN = "Generating counter-model... ";
    static final String GENERATE_FRJ_LATEX_MODEL_END = "saved in [%s]";

    static final String GENERATE_MAKEFILE_BEGIN = "Generating counter-model... ";
    static final String GENERATE_MAKEFILE_END = "saved in [%s]";
    static final String GENERATE_MAKEFILE_HINT =
        "Execute \"make\" in output directory to compile all output files";

  }

  static class LATEX_GENERATOR {

    static final String FILE_EXISTS_AND_IS_A_NOT_A_DIR = "[%s] exists and is not a directory.";
  }

}
