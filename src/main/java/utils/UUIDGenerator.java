package utils;

import java.util.UUID;

public class UUIDGenerator {

	public static String generateUuid() {
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		return randomUUIDString;
	}
}
