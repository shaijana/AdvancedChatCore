package io.github.darkkronicle.advancedchatcore.util

import java.util.*

class RowList<T> {

	private val list: MutableMap<String, MutableList<T>> = HashMap()
	private val order: MutableList<String> = ArrayList()

	private fun makeNewList(): MutableList<T> {
		return ArrayList()
	}

	fun createSection(key: String, y: Int) {
		val newList = makeNewList()
		order.add(y, key)
		list.put(key, newList)
	}

	@kotlin.jvm.JvmOverloads
	fun add(key: String, value: T, index: Int = -1) {
		if (!list.containsKey(key)) {
			val newList = makeNewList()
			order.add(key)
			newList.add(value)
			list.put(key, newList)
			return
		}
		if (index < 0) {
			list[key]!!.add(value)
		} else {
			list[key]!!.add(index, value)
		}
	}

	fun get(key: String): List<T> {
		return list[key]!!
	}

	fun get(y: Int): List<T> {
		val key = if (y >= order.size) {
			order[order.size - 1]
		} else {
			order[y]
		}
		return list[key]!!
	}

	fun rowSize(): Int {
		return list.size
	}

	fun clear() {
		order.clear()
		list.clear()
	}
}
