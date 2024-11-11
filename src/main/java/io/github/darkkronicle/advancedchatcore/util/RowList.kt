package io.github.darkkronicle.advancedchatcore.util

class RowList<T> {

	private val list = mutableMapOf<String, MutableList<T>>()
	private val order = mutableListOf<String>()

	private fun makeNewList(): MutableList<T> {
		return mutableListOf()
	}

	fun createSection(key: String, index: Int) {
		val newList = makeNewList()
		order[index] = key
		list[key] = newList
	}

	fun add(key: String, value: T, index: Int = -1) {
		if (!list.containsKey(key)) {
			val newList = makeNewList()
			order += key
			newList += value
			list[key] = newList
			return
		}
		if (index < 0) {
			list[key]?.add(value)
		} else {
			list[key]?.add(index, value)
		}
	}

	fun get(key: String): MutableList<T>? {
		return list[key]
	}

	fun get(y: Int): MutableList<T>? {
		val key = if (y >= order.size) {
			order[order.size - 1]
		} else {
			order[y]
		}
		return list[key]
	}

	fun rowSize(): Int {
		return list.size
	}

	fun clear() {
		order.clear()
		list.clear()
	}
}
