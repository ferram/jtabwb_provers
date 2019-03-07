package ipl.frj.launcher;

import ferram.rtoptions.NamedArgumentsSet;

public class SelectableProvers extends NamedArgumentsSet<AvailableProvers> {

  public SelectableProvers() {
    super(AvailableProvers.values());
  }

  @Override
  public AvailableProvers searchByName(String name) {
    return (AvailableProvers) super.searchByName(name);
  }
}
