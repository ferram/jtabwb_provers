package ipl.frj.launcher;

import jtabwb.launcher._ProblemReader;
import jtabwbx.problems.ProblemDescription;

public class ProblemReaderData {

  public ProblemReaderData(_ProblemReader problemReader, ProblemDescription problemDescription,
      long parsing_problem_start_time, long parsing_problem_end_time) {
    super();
    this.problemReader = problemReader;
    this.problemDescription = problemDescription;
    this.parsing_problem_start_time = parsing_problem_start_time;
    this.parsing_problem_end_time = parsing_problem_end_time;
  }

  private _ProblemReader problemReader;
  private ProblemDescription problemDescription;
  private long parsing_problem_start_time = 0;
  private long parsing_problem_end_time = -1;

  /**
   * @return the problemDescription
   */
  public ProblemDescription getProblemDescription() {
    return this.problemDescription;
  }

  /**
   * @return the problemReader
   */
  public _ProblemReader getProblemReader() {
    return this.problemReader;
  }

  /**
   * @return the parsing_problem_start_time
   */
  public long getParsing_problem_start_time() {
    return this.parsing_problem_start_time;
  }

  /**
   * @return the parsing_problem_end_time
   */
  public long getParsing_problem_end_time() {
    return this.parsing_problem_end_time;
  }

  /**
   * Returns the time required to parse the problem description in milliseconds.
   * 
   * @return the time needed to parse the input problem.
   */
  public long problemParsingTime() {
    return parsing_problem_end_time - parsing_problem_start_time;
  }
}
