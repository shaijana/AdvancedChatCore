package io.github.darkkronicle.advancedchatcore.konstruct

import io.github.darkkronicle.Konstruct.functions.ObjectFunction
import io.github.darkkronicle.Konstruct.nodes.Node
import io.github.darkkronicle.Konstruct.parser.ParseContext
import io.github.darkkronicle.Konstruct.parser.Result
import io.github.darkkronicle.Konstruct.type.IntegerObject
import io.github.darkkronicle.Konstruct.type.KonstructObject
import io.github.darkkronicle.Konstruct.type.StringObject
import io.github.darkkronicle.advancedchatcore.util.StringMatch

class StringMatchObject(private val result: StringMatch) : KonstructObject<StringMatchObject?>(FUNCTIONS) {

	val string: String
		get() {
			return result.toString()
		}

	val typeName: String
		get() {
			return "string_match"
		}

	companion object {

		private val FUNCTIONS: List<ObjectFunction<StringMatchObject>> = java.util.List.of(
			object : ObjectFunction() {
				override fun parse(context: ParseContext?, self: StringMatchObject, input: List<Node?>?): Result {
					return Result.success(IntegerObject(self.result.start))
				}

				val name: String
					get() {
						return "getStart"
					}

				val argumentCount: IntRange
					get() {
						return IntRange.of(0)
					}
			},
			object : ObjectFunction() {
				override fun parse(context: ParseContext?, self: StringMatchObject, input: List<Node?>?): Result {
					return Result.success(IntegerObject(self.result.end))
				}

				val name: String
					get() {
						return "getEnd"
					}

				val argumentCount: IntRange
					get() {
						return IntRange.of(0)
					}
			},
			object : ObjectFunction() {
				override fun parse(context: ParseContext?, self: StringMatchObject, input: List<Node?>?): Result {
					return Result.success(StringObject(self.result.match))
				}

				val name: String
					get() {
						return "getMessage"
					}

				val argumentCount: IntRange
					get() {
						return IntRange.of(0)
					}
			}
		)
	}
}
