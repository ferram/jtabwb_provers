package ipl.frj.launcher;

import ferram.rtoptions.NamedArgumentsSet;

public class SelectableSequentsDB extends NamedArgumentsSet<AvailableSequentDB> {

  public SelectableSequentsDB() {
    super(AvailableSequentDB.values());
  }

  @Override
  public AvailableSequentDB searchByName(String name) {
    return (AvailableSequentDB) super.searchByName(name);
  }
}
