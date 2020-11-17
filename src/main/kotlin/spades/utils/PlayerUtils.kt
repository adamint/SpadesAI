package spades.utils

import spades.engine.Hand
import spades.engine.SpadesGame
import spades.players.Player
import kotlin.random.Random

fun List<Player>.getTurnOrder(starter: Player): List<Player> {
    var starterIndex = 0
    while (this[starterIndex] != starter) starterIndex++

    return subList(starterIndex, size) + subList(0, starterIndex)
}

fun List<Player>.generateHands(): Map<Player, Hand> {
    val cards = allCards.toMutableList()
    cards.shuffle()

    val hands = mutableMapOf<Player, Hand>()
    forEach { player ->
        val handCards = cards.take(13).toMutableList()
        cards.removeAll(handCards)
        val hand = Hand(handCards, player)
        player.hand = hand
        hands[player] = hand
    }

    return hands
}

fun generateRandomPlayerOrder(teamOne: List<Player>, teamTwo: List<Player>): List<Player> {
    val teamOneTemp = teamOne.toMutableList()
    val teamTwoTemp = teamTwo.toMutableList()

    val order = mutableListOf<Player>()
    val first = teamOneTemp.random()
    val second = teamTwoTemp.random()
    teamOneTemp.remove(first)
    teamTwoTemp.remove(second)

    val firstStarts = Random.nextBoolean()

    order += if (firstStarts) listOf(first, second, teamOneTemp.first(), teamTwoTemp.first())
    else listOf(second, first, teamTwoTemp.first(), teamOneTemp.first())

    return order
}

fun List<Player>.without(player: Player) = toMutableList().apply { remove(player) }
fun List<Player>.withObservers(game: SpadesGame) = toMutableList().apply { addAll(game.observers) }