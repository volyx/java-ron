package ron;

import static ron.Const.*;

public class Spec {

	public Atom[] spec;

	public void setType(UUID uuid) {
		spec[SPEC_TYPE] = new Atom(uuid);
	}
	public void setObject(UUID uuid) {
		spec[SPEC_OBJECT] = new Atom(uuid);
	}
	public void setEvent(UUID uuid) {
		spec[SPEC_EVENT] = new Atom(uuid);
	}
	public void setRef(UUID uuid) {
		spec[SPEC_REF] = new Atom(uuid);
	}

}
