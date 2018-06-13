package ron;

import static ron.Const.*;

public class Spec {

	public Atom[] spec;

	public Spec(Atom[] atoms) {
		this.spec = atoms;
	}

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

	public Spec clone() {
		Atom[] atoms = new Atom[this.spec.length];
		for (int i = 0; i < atoms.length; i++) {
			atoms[i] = new Atom(this.spec[i]);
		}
		return new Spec(atoms);
	}
}
