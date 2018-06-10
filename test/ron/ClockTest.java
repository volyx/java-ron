package ron;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.format.SignStyle;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.time.temporal.ChronoField.*;

public class ClockTest {

	@Test
	public void TestClock_Format() throws ParseException {

//		SimpleDateFormat UNIX_FORMAT = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");
		SimpleDateFormat UNIX_FORMAT = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy");
		var tests = new String[][]{
				// "Thu Jun 18 20:56:02 EDT 2009";
			{"Fri Jan  1 00:00:00 UTC 2010", "0"},
			{"Sat May  1 01:02:00 UTC 2010", "04012"},
			{"Fri May 27 20:50:00 UTC 2016", "1CQKn"},
		};

		for (int i = 0; i < tests.length; i++) {
			var pair = tests[i];
//			date, err := ZonedDateTime.parse(time.UnixDate, pair[0])
			final Date dateTime = UNIX_FORMAT.parse(pair[0]);
			ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochMilli(dateTime.getTime()), ZoneOffset.UTC);

			long ui = Clock.encodeCalendar(date);
			Slice s = FrameAppend.formatInt(new Slice(new byte[]{}), ui);
			var str = s.string();
			if (!Objects.equals(str, pair[1])) {
				Assert.fail(String.format("case %d: %s must be %s", i, str, pair[1]));
			}
			ZonedDateTime t2 = Clock.decodeCalendar(ui);
			String str2 = t2.format(DateTimeFormatter.ISO_DATE_TIME);
			if (!str2.equals(pair[0])) {
				Assert.fail(String.format("case %d: %s must be %s", i, str2, pair[0]));
			}
		}



	}

}
