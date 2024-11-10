/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.util

/** A class to handle an easing method. Examples at https://easings.net/  */
interface EasingMethod {

	/**
	 * Applies the current percentage of the ease.
	 *
	 * @param x Double from 0-1 (will often clamp at those values)
	 * @return The easing value (often clamped at 0-1)
	 */
	fun apply(x: Double): Double

	/** Useful easing methods  */
	enum class Method(private val method: EasingMethod) : EasingMethod {

		/** Implements the linear easing function. It returns the same value. x = x  */
		LINEAR(EasingMethod { x: Double -> x }),

		/**
		 * Implements the sine easing function.
		 *
		 *
		 * https://easings.net/#easeInSine
		 */
		SINE(EasingMethod { x: Double -> 1 - cos((x * Math.PI) / 2) }),

		/**
		 * Implements the quad easing function.
		 *
		 *
		 * https://easings.net/#easeInQuad
		 */
		QUAD(EasingMethod { x: Double -> x * x }),

		/**
		 * Implements the quart easing function.
		 *
		 *
		 * https://easings.net/#easeInQuart
		 */
		QUART(EasingMethod { x: Double -> x * x * x * x }),

		/**
		 * Implements the circ easing function.
		 *
		 *
		 * https://easings.net/#easeInCirc
		 */
		CIRC(EasingMethod { x: Double -> 1 - sqrt(1 - x.pow(2)) });

		override fun apply(x: Double): Double {
			if (x < 0) {
				return 0
			} else if (x > 1) {
				return 1
			}
			return method.apply(x)
		}
	}
}
