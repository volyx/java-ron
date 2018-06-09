package ron;

public class SerializeState {
    // uint
	long Format;

	public SerializeState(){
		this.Format = 0L;
	}

	public SerializeState(SerializeState serializer) {
		this.Format = serializer.Format;
	}

	public SerializeState(long format) {
		Format = format;
	}
}