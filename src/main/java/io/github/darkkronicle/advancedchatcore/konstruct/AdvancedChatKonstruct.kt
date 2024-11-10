package io.github.darkkronicle.advancedchatcore.konstruct

import io.github.darkkronicle.Konstruct.functions.NamedFunction
import io.github.darkkronicle.Konstruct.functions.Variable
import io.github.darkkronicle.Konstruct.nodes.Node
import io.github.darkkronicle.Konstruct.reader.builder.NodeBuilder
import io.github.darkkronicle.Konstruct.type.IntegerObject
import io.github.darkkronicle.Konstruct.type.StringObject
import io.github.darkkronicle.advancedchatcore.AdvancedChatCore
import io.github.darkkronicle.advancedchatcore.util.Color
import io.github.darkkronicle.advancedchatcore.util.Colors
import lombok.Getter
import net.minecraft.util.Util

class AdvancedChatKonstruct private constructor() {

	@Getter
	private var processor: NodeProcessor? = null

	init {
		reset()
		addFunction(CalculatorFunction())
		addFunction(RandomFunction())
		addFunction(ReplaceFunction())
		addFunction(RoundFunction())
		addFunction(OwOFunction())
		addFunction(RomanNumeralFunction())
		addFunction(IsMatchFunction())
		addFunction(TimeFunction())
		addVariable("server", Variable { StringObject(AdvancedChatCore.Companion.getServer()) })
		addFunction("randomString", object : Function() {
			override fun parse(context: ParseContext?, input: List<Node?>?): Result {
				return Result.success(AdvancedChatCore.Companion.getRandomString())
			}

			val argumentCount: IntRange
				get() {
					return IntRange.none()
				}
		})
		addFunction("getColor", object : Function() {
			override fun parse(context: ParseContext?, input: List<Node?>?): Result {
				val res: Result = Function.parseArgument(context, input, 0)
				if (Function.shouldReturn(res)) return res
				val color: Color = Colors.Companion.getInstance().getColorOrWhite(res.getContent().getString())
				return Result.success(color.getString())
			}

			val argumentCount: IntRange
				get() {
					return IntRange.of(1)
				}
		})
		addFunction("superscript", object : Function() {
			override fun parse(context: ParseContext?, input: List<Node?>?): Result {
				val number: Int
				try {
					val res: Result = Function.parseArgument(context, input, 0)
					if (Function.shouldReturn(res)) return res
					number = res.getContent().getString().strip().toInt()
				} catch (e: NumberFormatException) {
					return Result.success("NaN")
				}
				return Result.success(toSuperscript(number))
			}

			val argumentCount: IntRange
				get() {
					return IntRange.of(1)
				}
		})
		addVariable("ms", Variable { IntegerObject(Util.getMeasuringTimeMs().toInt()) })
	}

	fun parse(node: Node?): ParseResult {
		return processor.parse(node)
	}

	fun getNode(string: String?): Node {
		return NodeBuilder(string).build()
	}

	fun copy(): NodeProcessor {
		return processor.copy()
	}

	/** Not really recommended to call...  */
	fun reset() {
		this.processor = NodeProcessor()
	}

	fun addVariable(key: String?, variable: Variable?) {
		processor.addVariable(key, variable)
	}

	fun addFunction(function: NamedFunction?) {
		processor.addFunction(function)
	}

	fun addFunction(key: String?, function: Function?) {
		processor.addFunction(key, function)
	}

	companion object {

		val instance: AdvancedChatKonstruct = AdvancedChatKonstruct()
	}
}
