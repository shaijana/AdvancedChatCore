/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.util

import lombok.AllArgsConstructor
import lombok.Value
import java.util.*

// Referenced
// https://github.com/vacla/Watson/blob/fabric_1.16.2/src/main/java/eu/minemania/watson/scheduler/SyncTaskQueue.java
/** A queue to handle delayed tasks in ticks.  */
class SyncTaskQueue {

	private var lastTick = 0

	/** A task that contains a time to trigger and a [Runnable] for when it should happen.  */
	@Value
	@AllArgsConstructor
	class QueuedTask : Comparable<QueuedTask> {

		/**
		 * Tick number when it should be triggered. This isn't delay, this is based off of the
		 * current tick value in [net.minecraft.client.gui.hud.InGameHud]
		 */
		var tick: Int = 0

		/** [Runnable] to run when the task is called.  */
		var task: Runnable? = null

		override fun compareTo(@NotNull o: QueuedTask): Int {
			// Compares when it should happen. Used to ensure that the first in the stack is what
			// needs to
			// happen.
			return Integer.compare(tick, o.tick)
		}
	}

	// Use TreeSet to automagically sort by when it needs to happen
	private val queue = TreeSet<QueuedTask>()

	/**
	 * Add's a new task to do after a certain amount of ticks
	 *
	 * @param after Delay in ticks
	 * @param runnable What to run when it should be called
	 */
	fun add(after: Int, runnable: Runnable) {
		queue.add(QueuedTask(lastTick + after, runnable))
	}

	/**
	 * Updates the queue with the tick. This shouldn't be called outside of the core.
	 *
	 * @param tick Current time in ticks.
	 */
	fun update(tick: Int) {
		lastTick = tick
		if (queue.size == 0) {
			return
		}
		var task = queue.first()
		while (task != null && task.tick <= lastTick) {
			task.task!!.run()
			queue.pollFirst()
			if (queue.size == 0) {
				break
			}
			task = queue.first()
		}
	}

	companion object {

		val instance: SyncTaskQueue = SyncTaskQueue()
	}
}
