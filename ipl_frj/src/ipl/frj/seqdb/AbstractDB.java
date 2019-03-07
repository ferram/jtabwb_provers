package ipl.frj.seqdb;

import java.util.Collection;

import ipl.frj.launcher.LauncherExecConfiguration;
import ipl.frj.sequent.FrjIrregularSequent;
import ipl.frj.sequent.FrjRegularSequent;
import ipl.frj.sequent._FrjSequent;
import ipl.frj.tp.basic.CandidateFormulas;
import ipl.frj.tp.basic.ProvedSequentsTables;
import ipl.frj.tp.basic._ProvedSequentsDB;
import ipl.frj.tp.basic._SequentsTable;
import ipl.frj.util.MSGManager;
import jtabwb.util.ImplementationError;
import jtabwbx.prop.formula.Formula;

/**
 * An implementation of the database storing the proved sequents; this
 * implementation does perform any subsmption check when adding a proved
 * sequent.
 * 
 * 
 * The DB maintains a table for every iteration performed by the prover and a
 * global table storing all the proved sequents. The {@link #add(_FrjSequent)}
 * method add a sequent to the <em>current iteration</em> table. The sequent is
 * effectively added only if it has not been previously added to the DB. The
 * sequents added to the current iteration table are not stored in the global
 * table until the {@link #commitIteration()} method is invoked; when this
 * method is invoked a new fresh iteration table is created (the new <em>current
 * iteration</em> table). The class provides methods to access the sequents
 * stored in the main tables maintained by the DB, that is: the <em>global
 * table</em>, in the <em>current iteration</em> table, and the
 * <em>last-committed iteration</em> table; available tables are specified by
 * the {@link ProvedSequentsTables} enumerative constants.
 * 
 * @author Mauro Ferrari
 *
 */
abstract class AbstractDB implements _ProvedSequentsDB {

  private final int ITERATION_MAX = 100;
  boolean VERBOSE = false;

  public AbstractDB(CandidateFormulas candidateFormulas, _SequentsTable globalTable,
      _SequentsTable firstIterationTable, LauncherExecConfiguration execCofiguration) {
    super();
    this.VERBOSE = execCofiguration.verboseMode();
    this.candidateFormulas = candidateFormulas;
    this.goal = candidateFormulas.getGoal();
    this.iterationTables = new _SequentsTable[ITERATION_MAX];
    this.currentIterationTable = firstIterationTable;
    this.globalTable = globalTable;
  }

  private CandidateFormulas candidateFormulas;
  private _SequentsTable currentIterationTable;
  private _SequentsTable globalTable;
  private int idxProvedSequents = 0;

  //to manage iterations 
  private int currentIteration = 0;
  private _SequentsTable[] iterationTables;
  private Formula goal;
  private int lastAddedIteration = -1;

  /**
   * Commits the sequents proved in the last iteration to the global table.
   * 
   */
  @Override
  public void commitIteration() {
    if (VERBOSE)
      MSGManager.infoNoLn(MSG.VERBOSE.COMMITTING_BEGIN);
    lastAddedIteration = currentIteration;
    iterationTables[currentIteration] = currentIterationTable;

    int countIrregularCommitted = 0;
    Collection<FrjIrregularSequent> irregular = currentIterationTable.irregular();
    if (irregular != null)
      for (FrjIrregularSequent seq : irregular) {
        globalTable.insert(seq);
        countIrregularCommitted++;
      }

    int countRegularCommitted = 0;
    Collection<FrjRegularSequent> regular = currentIterationTable.regular();
    if (regular != null)
      for (FrjRegularSequent seq : regular) {
        globalTable.insert(seq);
        countRegularCommitted++;
      }

    if (VERBOSE)
      MSGManager.info(MSG.VERBOSE.COMMITTING_END, countIrregularCommitted, countRegularCommitted);

    currentIteration++;
    currentIterationTable = geneateNewIterationTable();
  }

  /**
   * Build a new iteration table.
   * 
   * @return a new iteration table.
   */
  abstract _SequentsTable geneateNewIterationTable();

  /**
   * Add the specified sequent to those proved in the current iteration provided
   * that the specified sequent does not already belong to the iteration table
   * or to the global table.
   * 
   * @param provedSequent the sequent to add.
   * @return true iff the sequent has been effectoively added to the current
   * iteration table.
   */
  @Override
  public boolean add(_FrjSequent provedSequent) {

    if (globalTable.contains(provedSequent))
      return false;

    if (currentIterationTable.contains(provedSequent))
      return false;

    currentIterationTable.insert(provedSequent);
    provedSequent.setSequentProgressiveNumber(idxProvedSequents++);

    return true;

    
  }
  
  @Override
  public CandidateFormulas candidateFormulas(){
    return candidateFormulas;
  }

  /**
   * Proof-search succeed if a regular sequent having goal in the right hand
   * side has been generated.
   * 
   * @return <code>true</code> if proof-search succeed.
   */
  @Override
  public boolean checkForSuccess() {
    return globalTable.regularWithRight(goal) != null;
  }

  /**
   * Returns the regular sequents in the global table proving the goal.
   * 
   * @return the collection of the sequents proving the goal contained in the
   * global table.
   */
  @Override
  public Collection<FrjRegularSequent> getSuccessSequents() {
    return globalTable.regularWithRight(goal);
  }

  /**
   * Return all the irregular sequents added to the specified table.
   * 
   * @return the irregular sequents contained in the specified table.
   */
  @Override
  public Collection<FrjIrregularSequent> irregular(ProvedSequentsTables table) {
    switch (table) {
    case GLOBAL:
      return globalTable.irregular();
    case LAST_COMMITTED_ITERATION:
      return iterationTables[lastAddedIteration].irregular();
    case CURRENT_ITERATION:
      return currentIterationTable.irregular();
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED_arg, table);
    }
  }

  /**
   * Return the regular sequents added to the specified table.
   * 
   * @return the regular sequents added to specified table.
   */
  @Override
  public Collection<FrjRegularSequent> regular(ProvedSequentsTables table) {
    switch (table) {
    case GLOBAL:
      return globalTable.regular();
    case LAST_COMMITTED_ITERATION:
      return iterationTables[lastAddedIteration].regular();
    case CURRENT_ITERATION:
      return currentIterationTable.regular();
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED_arg, table);
    }

  }

  /**
   * Returns all the irregular sequents with the specified right formula
   * contained in the specifed table.
   * 
   * @return the irregular sequents with the specified right formula contained
   * in the specified table.
   */
  @Override
  public Collection<FrjIrregularSequent> irregularWithRight(ProvedSequentsTables table, Formula wff) {
    switch (table) {
    case GLOBAL:
      return globalTable.irregularWithRight(wff);
    case LAST_COMMITTED_ITERATION:
      return iterationTables[lastAddedIteration].irregularWithRight(wff);
    case CURRENT_ITERATION:
      return currentIterationTable.irregularWithRight(wff);
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED_arg, table);
    }
  }

  /**
   * Returns all the regular sequents with the specified right formula contained
   * in the specified table.
   * 
   * @return the regular sequents with the specified right formula contained in
   * the specified table.
   */
  @Override
  public Collection<FrjRegularSequent> regularWithRight(ProvedSequentsTables table, Formula wff) {
    switch (table) {
    case GLOBAL:
      return globalTable.regularWithRight(wff);
    case LAST_COMMITTED_ITERATION:
      return iterationTables[lastAddedIteration].regularWithRight(wff);
    case CURRENT_ITERATION:
      return currentIterationTable.regularWithRight(wff);
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED_arg, table);
    }
  }

  /**
   * Get the table storing the sequents proved at the specified iteration.
   * 
   * @param iteration
   * @return the table storing the sequents proved at the specified iteration.
   */
  @Override
  public _SequentsTable getIterationTable(int iteration) {
    if (iteration > lastAddedIteration)
      throw new ImplementationError(MSG.ERRORS.ITERATION_REQ_ERROR, iteration);
    else
      return iterationTables[iteration];
  }

  /**
   * The total number of proved sequents in the specified table.
   * 
   * @param table
   * @return the total number of proved sequents in the specified table.
   */
  @Override
  public int numberOfProvedSequents(ProvedSequentsTables table) {
    switch (table) {
    case GLOBAL:
      return globalTable.numberOfProvedSequents();
    case LAST_COMMITTED_ITERATION:
      return iterationTables[lastAddedIteration].numberOfProvedSequents();
    case CURRENT_ITERATION:
      return currentIterationTable.numberOfProvedSequents();
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED_arg, table);
    }
  }

  /**
   * The current iteration counter.
   * 
   * @return the current iteration counter.
   */
  @Override
  public int currentIterarion() {
    return currentIteration;
  }

  /**
   * The last committed iteration counter.
   * 
   * @return the last commit iteration counter.
   */
  @Override
  public int lastCommittedIteration() {
    return lastCommittedIteration();
  }

  
  _SequentsTable getGlobalTable() {
    return globalTable;
  }
  
  _SequentsTable getCurrentIterationTable(){
    return currentIterationTable;
  }

  @Override
  public int setSequentIdx(_FrjSequent seq) {
    int idx = idxProvedSequents++;
    seq.setSequentProgressiveNumber(idx);
    return idx;
  }
  
  void generateNewIterationTable(){
    lastAddedIteration = currentIteration;
    iterationTables[currentIteration] = currentIterationTable;
    currentIteration++;
    currentIterationTable = geneateNewIterationTable();
  }
  

}
