package ipl.frj.seqdb;

class MSG {

  static class VERBOSE {

    static String SEQUENT_DETAILS = "    Rule [%s]: [%s]";
    static String COMMITTING_BEGIN = "  Committing proved sequets to the DB...";
    static String COMMITTING_END = "  committed: [%d] irregular [%d] regular";
    static String COMMITTED_REGULAR= "  Committed Regular Sequent: idx [%d] --  [%s]";
    static String COMMITTED_IRREGULAR= "  Committed Irregular Sequent: idx [%d] --  [%s]";
    
    static String BUILDING_JOIN_PREMISES_BEGIN = "  Building Join Premises...";
    static String BUILDING_JOIN_PREISES_END = " [%d] join premises";
    static String DB_CONTAINS_SUCCESS_SEQUENTS = "  DB contains success sequents";
  }

  static class ERRORS {

    static String ITERATION_REQ_ERROR = "Iteration [%d] is not defined.";
  }

  static class BW_COMPATIBILITY_TABLE {

    public static class INFO {

      public static String CHECKING_SBW_SUBSUMPTION_BEGIN =
          "Checking backward subsumption for new irrregular...";
      public static String CHECKING_SBW_SUBSUMPTION_END = "[%d] subsumptions found";
    }

    public static class DEBUG {

      public static String COMPATIBILITY_TABLE_ADDED =
          "Compatibility table: [%s] irregular added with idx [%d] -- [%s]";
      public static String COMPATIBLE_SETS = "Compatible sets (total [%d]): [%s]";
      public static String COMPATIBILITY_CHECK =
          "Compatibility check: result [%s], sequents\n--[%s]\n--[%s]";
      public static String OLD_SET = "set: %s --> %s";

    }

  }

  static class BW_SUMPTION_DB {

    public static class DEBUG {

      public static String SUBSUMED_TABLE = "Sumbsumed table: %s";
    }

  }

  static class BW_ITERATION_TABLE {

    public static class DEBUG {

      public static String SUBSUMED_REMOVED = "Iteration table - subsumed removed [%s]";

    }
  }
  
  static class BD_BACKWARD_SUBSUMPTION {
    
    public static class VERBOSE {
      public static String REMOVING_IRREGULAR_SUBSUMED_BEGIN = "Removing subsumed irregular sequents from global table... ";
      public static String REMOVING_IRREGULAR_SUBSUMED_END= "[%d] removed.";
      
      
      public static String REMOVING_REGULAR_SUBSUMED_BEGIN = "Removing subsumed regular sequents from global table... ";
      public static String REMOVING_REGULAR_SUBSUMED_END= "[%d] removed.";
    
    }
  }

}
