package com.centralbank.utils;

import java.util.UUID;

public class UUIDGenerator {

	private UUIDGenerator() {

	}

	public static String generateUuid() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().substring(0, 5);
	}
}
