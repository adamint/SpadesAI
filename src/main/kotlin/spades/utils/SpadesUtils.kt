package spades.utils

import spades.models.Card
import spades.models.CardSuit
import spades.models.CardValue

val allCards = CardSuit.values().map { suit ->
    CardValue.values().map { value ->
        Card(suit, value)
    }
}.flatten()

fun Pair<Int, Int>.sum() = first + second

class BetConstants {
    companion object {
        const val BlindNil: Int = 200
        const val Nil: Int = 0
    }
}

fun <T> Pair<T, T>.indexed(index: Int) = if (index == 0) first else second