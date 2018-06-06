package ron;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class ParseTest {

	@Test
	public void TestParseUUID() {
//		func TestParseUUID(t *testing.T) {
			UUID uuidA = Parse.parseUUID("1".getBytes(StandardCharsets.UTF_8));
			if (uuidA.value() != (1L << 54) || uuidA.origin() != 0 || uuidA.scheme() != Const.UUID_NAME) {
				Assert.fail( "Fail");
			}
			UUID uuidAB = Parse.parseUUID(")1".getBytes(StandardCharsets.UTF_8));
			if ((uuidAB.value() != ((1L << 54) | 1L)) || uuidAB.origin() != 0 || uuidAB.scheme() != Const.UUID_NAME) {
				Assert.fail( "Fail");
			}
//			hello, _ := ParseUUID([]byte("hello-111"))
//			world, _ := hello.Parse([]byte("[world-111"))
//			helloworld, _ := ParseUUID([]byte("helloworld-111"))
//			if !world.Equal(helloworld) {
//				t.Fail()
//			}
//			err_str := "erro_error$~~~~~~~~~~"
//			error_uid, err := ParseUUIDString(err_str)
//			if err != nil || error_uid.IsZero() {
//				t.Fail()
//			}
//		}
	}
}
