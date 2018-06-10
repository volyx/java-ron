package ron;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static ron.Const.UUID_EVENT;
import static ron.Frame.PREFIX_MASKS;
import static ron.UUID.INT60_FULL;

// hybrid calendar/logical clock
public class Clock {

	public static final UUID CLOCK_CALENDAR = UUID.newName("Calendar");
	public static final UUID CLOCK_EPOCH = UUID.newName("Epoch"); // TODO implement behavior
	public static final UUID CLOCK_LAMPORT = UUID.newName("Logical");

	public static final long MAX_BIT_GRAB = 1L << 20;

	private int offset;
	private UUID lastSeen;
	private UUID mode;
	private int minLength;

	public static Clock newClock(long replica, UUID mode, int minLen) {
		long origin = (replica & INT60_FULL) | (((long)UUID_EVENT) << 60);
		Clock clock = new Clock();
		clock.lastSeen = UUID.newEventUUID(0, origin);
		clock.mode = mode;
		clock.minLength = minLen;
		return clock;
	}

	public static long encodeCalendar(ZonedDateTime t) {
		long months = (t.getYear() - 2010) * 12 + t.getMonthValue() - 1;
		long i = months;
		long days = t.getDayOfMonth() - 1;
		i = i << 6;
		i = i | days;
		long hours = t.getHour();
		i = i << 6;
		i = i |hours;
		long minutes = t.getMinute();
		i = i << 6;
		i = i << minutes;
		long seconds = t.getSecond();
		i = i << 6;
		i = i << seconds;
		long micros = t.getNano() / 100;
		i = i << 24;
		i = i | micros;
		return i;
	}

	public byte[] calendarToRFC(UUID uuid) 	{
		// the formula comes from satori/go.uuid
		ZonedDateTime time = decodeCalendar(uuid.value());
//		timeRfc := uint64(122192928000000000) + uint64(time.UnixNano()/100)
//		timeRfc := 122192928000000000L + (long) (time.toInstant().toEpochMilli() / 100)
//		binary.BigEndian.PutUint32(u[0:], uint32(timeRfc))
//		binary.BigEndian.PutUint16(u[4:], uint16(timeRfc>>32))
//		binary.BigEndian.PutUint16(u[6:], uint16(timeRfc>>48))
//		binary.BigEndian.PutUint16(u[8:], 0)
//
//		var replicaId [6]byte
//		orig := uuid.Origin()
//		orig >>= 60 - 6*8
//		for i := 0; i < 6; i++ {
//			replicaId[5-i] = byte(orig & 255)
//			orig >>= 8
//		}
//
//		copy(u[10:], replicaId[:])
//		var version byte = 1
//		u[6] = (u[6] & 0x0f) | (version << 4)
//		u[8] = (u[8] & 0xbf) | 0x80
//		return u
		return new byte[0];
	}

//	public String calendarToRFCString(UUID uuid) {
//		u := calendarToRFC(uuid);
//		buf := make([]byte, 36)
//
//		hex.Encode(buf[0:8], u[0:4])
//		buf[8] = '-'
//		hex.Encode(buf[9:13], u[4:6])
//		buf[13] = '-'
//		hex.Encode(buf[14:18], u[6:8])
//		buf[18] = '-'
//		hex.Encode(buf[19:23], u[8:10])
//		buf[23] = '-'
//		hex.Encode(buf[24:], u[10:])
//
//		return string(buf)
//
//	}

	private long trim_time(long full, long last) {
		var i = 5;
		for (;i < 11 && ((full & PREFIX_MASKS[i]) <= last);) {
			i++;
		}
		return full & PREFIX_MASKS[i];
	}

	public UUID time() {
		long val;
		long last = this.lastSeen.value();
		ZonedDateTime t;
		if (CLOCK_CALENDAR.equals(this.mode)) {
			t = LocalDateTime.now().atOffset(ZoneOffset.ofHours(this.offset)).toLocalDateTime().atZone(ZoneOffset.UTC);
			val = encodeCalendar(t);

		} else if (CLOCK_LAMPORT.equals(this.mode)) {
			val = last + 1;

		} else if (CLOCK_EPOCH.equals(this.mode)) {
			t = LocalDateTime.now().atOffset(ZoneOffset.ofHours(this.offset)).toLocalDateTime().atZone(ZoneOffset.UTC);
			val = t.toEpochSecond() << (4 * 6); // TODO define

		} else {
			throw new RuntimeException();
		}
		if (val <= last) {
			val = last + 1;
		} else {
			val = trim_time(val, last);
		}
		UUID ret = UUID.newEventUUID(val, this.lastSeen.origin());
		this.see(ret);
		return ret;
	}

	public boolean see(UUID uuid) {
		if (!this.isSane(uuid)) {
			return false;
		}
		if (this.lastSeen.value() < uuid.value()) {
			this.lastSeen = UUID.newEventUUID(uuid.value(), this.lastSeen.origin());
		}
		return true;
	}

	public boolean isSane(UUID uuid) {
		if (CLOCK_LAMPORT.equals(this.mode)) {
			return this.lastSeen.value() + MAX_BIT_GRAB > uuid.value();
		} else {
			return true;
		}
	}

	public ZonedDateTime decode(UUID uuid) {
		if (CLOCK_CALENDAR.equals(this.mode)) {
			return decodeCalendar(uuid.value());
		} else {
			return ZonedDateTime.now();
		}

	}

	private static final long MASK24 = 16777215;

	public static ZonedDateTime decodeCalendar(long v) {
		int ns100 = (int) (v & MASK24);
		v = v >> 24;
		int secs = (int) (v & 63);
		v = v >> 6;
		int mins = (int) (v & 63);
		v = v >> 6;
		int hours = (int) (v & 63);
		v = v >> 6;
		int days = (int) (v & 63);
		v = v >> 6;
		int months = (int) (v & 4095);
		var month = months % 12;
		var year = months / 12;
		return LocalDateTime.of(year + 2010, month + 1, days + 1, hours, mins, secs, ns100 * 100).atZone(ZoneOffset.UTC);
	}
}
