/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.interfaces

import com.google.gson.JsonObject

/**
 * Json serializer for other classes.
 *
 * @param <T> Class that this class serializes.
</T> */
interface IJsonSave<T> {

	/**
	 * Returns a new object from a JsonObject to deserialize from.
	 *
	 * @param obj Object containing serialized data
	 * @return Constructed object
	 */
	fun load(obj: JsonObject?): T

	/**
	 * Takes an object (T) and serializes it.
	 *
	 * @param t Object to serialize
	 * @return Serialized [JsonObject]
	 */
	fun save(t: T): JsonObject?
}
