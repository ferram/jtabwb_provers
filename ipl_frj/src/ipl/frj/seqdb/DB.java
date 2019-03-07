package ipl.frj.seqdb;

import ipl.frj.launcher.LauncherExecConfiguration;
import ipl.frj.sequent._FrjSequent;
import ipl.frj.tp.basic.CandidateFormulas;
import ipl.frj.tp.basic.ProvedSequentsTables;
import ipl.frj.tp.basic._SequentsTable;

/**
 * A plain implementation of the database storing the proved sequents; this
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
public class DB extends AbstractDB {

  public DB(CandidateFormulas candidateFormulas, LauncherExecConfiguration execCofiguration) {
    super(candidateFormulas, new SequentsTable(execCofiguration),
        new SequentsTable(execCofiguration), execCofiguration);
    this.execConfiguration = execCofiguration;
  }

  private LauncherExecConfiguration execConfiguration;

  @Override
  _SequentsTable geneateNewIterationTable() {
    return new SequentsTable(execConfiguration);
  }

}
