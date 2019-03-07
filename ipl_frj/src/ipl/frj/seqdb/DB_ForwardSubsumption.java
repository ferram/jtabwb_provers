package ipl.frj.seqdb;

import ipl.frj.launcher.LauncherExecConfiguration;
import ipl.frj.sequent._FrjSequent;
import ipl.frj.tp.basic.CandidateFormulas;
import ipl.frj.tp.basic.ProvedSequentsTables;
import ipl.frj.tp.basic._SequentsTable;

/**
 * An implementation of the database storing the proved sequents performing
 * forward subsumption check when adding a proved sequent.
 * 
 * The database storing the proved sequents. The DB maintains a table for every
 * iteration performed by the prover and a global table storing all the proved
 * sequents. The {@link #add(_FrjSequent)} method add a sequent to the <em>current
 * iteration</em> table only if the added sequent is not subsumed by a sequent
 * already contained either in the <em>global table</em> or in the
 * <em>current-iteration</em> table.
 * 
 * 
 * The sequent is effectively added only if it has not been previously added to
 * the DB. The sequents added to the current iteration table are not stored in
 * the global table until the {@link #commitIteration()} method is invoked; when
 * this method is invoked a new fresh iteration table is created (the new
 * <em>current iteration</em> table). The class provides methods to access the
 * sequents stored in the main tables maintained by the DB, that is: the
 * <em>global table</em>, in the <em>current iteration</em> table, and the
 * <em>last-committed iteration</em> table; available tables are specified by
 * the {@link ProvedSequentsTables} enumerative constants.
 * 
 * @author Mauro Ferrari
 *
 */
public class DB_ForwardSubsumption extends AbstractDB {

  public DB_ForwardSubsumption(CandidateFormulas candidateFormulas,
      LauncherExecConfiguration execCofiguration) {
    super(candidateFormulas, new SequentsTable(execCofiguration),
        new SequentsTableRemovingSubsumed(execCofiguration), execCofiguration);
    this.execConfiguration = execCofiguration;
    this.globalTable = (SequentsTable) getGlobalTable();
  }

  private LauncherExecConfiguration execConfiguration;
  private SequentsTable globalTable;

  /**
   * Add the specified sequent to those proved in the current iteration provided
   * that the specified sequent is not subsumed by a sequent already stored in
   * the <em>global table</em> or in the <em>current-iteration</em> table.
   * 
   * @param provedSequent the sequent to add.
   * @return true iff the sequent has been effectively added to the current
   * iteration table.
   */
  @Override
  public boolean add(_FrjSequent provedSequent) {
    SequentsTableRemovingSubsumed currentIterationTable =
        (SequentsTableRemovingSubsumed) super.getCurrentIterationTable();

    if (globalTable.contains(provedSequent) || currentIterationTable.contains(provedSequent))
      return false;

    if (globalTable.subsumes(provedSequent) || currentIterationTable.subsumes(provedSequent))
      return false;

    currentIterationTable.insert(provedSequent);
    this.setSequentIdx(provedSequent);
    return true;
  }

  @Override
  _SequentsTable geneateNewIterationTable() {
    return new SequentsTableRemovingSubsumed(execConfiguration);
  }

}
