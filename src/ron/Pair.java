package ron;



import java.util.Objects;

public final class Pair<L, R> {
	private static final Pair<Object, Object> EMPTY = new Pair<>((Object)null, (Object)null);
	private final L left;
	private final R right;

	public static <L, R> Pair<L, R> empty() {
		return (Pair<L, R>) EMPTY;
	}

	public static <L, R> Pair<L, R> createLeft(L left) {
		return left == null ? empty() : new Pair<>(left,null);
	}

	public static <L, R> Pair<L, R> createRight(R right) {
		return right == null ? empty() : new Pair<>(null, right);
	}

	public static <L, R> Pair<L, R> create(L left, R right) {
		return right == null && left == null ? empty() : new Pair<>(left, right);
	}

	private Pair(L left, R right) {
		this.left = left;
		this.right = right;
	}

	public L getLeft() {
		return this.left;
	}

	public R getRight() {
		return this.right;
	}

	public int hashCode() {
		return Objects.hashCode(this.left) + 31 * Objects.hashCode(this.right);
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof Pair)) {
			return false;
		} else {
			Pair<L, R> pair = (Pair) obj;
			return Objects.equals(this.left, pair.left) && Objects.equals(this.right, pair.right);
		}
	}

	public String toString() {
		return String.format("(%s, %s)", this.left, this.right);
	}
}
