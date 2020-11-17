package spades.engine

import spades.players.Player
import spades.utils.generateHands
import spades.utils.getTurnOrder
import spades.utils.withObservers
import spades.utils.without


data class Round(val hands: Map<Player, Hand>, val dealer: Player, @Transient val game: SpadesGame) : GameEvent {
    val tricks: MutableList<Trick> = mutableListOf()
    val bets: MutableList<Int> = mutableListOf()
    val roundsBefore = game.rounds.toList()

    var currentTrick: Trick = Trick(dealer, this)

    fun getTricksWonByPlayer() = tricks
        .map { it.getPlayerForCard(it.winningCard) to it }
        .groupBy { it.first }
        .map { group -> group.key to group.value.map { it.second } }
        .toMap()
        .toMutableMap()
        .apply {
            currentTrick.turnOrder.forEach { putIfAbsent(it, listOf()) }
        }

    fun getBets(): Map<Player, Int> = bets
        .mapIndexed { index, bet -> currentTrick.turnOrder[index] to bet }
        .toMap()

    val teamOneBet get() = getBets().getValue(game.teamOne[0]) to getBets().getValue(game.teamOne[1])
    val teamTwoBet get() = getBets().getValue(game.teamTwo[0]) to getBets().getValue(game.teamTwo[1])

    val teamOneMade
        get() = getTricksWonByPlayer().getValue(game.teamOne[0]).size to getTricksWonByPlayer().getValue(
            game.teamOne[1]
        ).size
    val teamTwoMade
        get() = getTricksWonByPlayer().getValue(game.teamTwo[0]).size to getTricksWonByPlayer().getValue(
            game.teamTwo[1]
        ).size

    val roundTeamOneScore: TeamRoundScore by lazy {
        TeamRoundScore(
            teamOneBet,
            teamOneMade,
            roundsBefore.lastOrNull()?.roundTeamOneScore
        )
    }
    val roundTeamTwoScore: TeamRoundScore by lazy {
        TeamRoundScore(
            teamTwoBet,
            teamTwoMade,
            roundsBefore.lastOrNull()?.roundTeamTwoScore
        )
    }

    val isRoundOver get() = tricks.size == 13

    var spadesEnabled = false

    override fun onStart() {
        currentTrick.turnOrder.withObservers(game).forEach { player -> player.onRoundStart() }
        while (bets.size != 4) {
            val bettingPlayer = currentTrick.turnOrder[bets.size]
            currentTrick.turnOrder.without(bettingPlayer).withObservers(game)
                .forEach { player -> player.onOtherBetRequested(bettingPlayer, this) }
            val bet = bettingPlayer.onBetRequested(hands.getValue(bettingPlayer), this) ?: 0
            bets.add(bet)
            currentTrick.turnOrder.without(bettingPlayer).withObservers(game)
                .forEach { player -> player.onOtherBetMade(bet, bettingPlayer, this) }
        }
        currentTrick.turnOrder.withObservers(game).forEach { player -> player.onBetsFinished(getBets(), this) }

        while (tricks.size != 13) {
            currentTrick.onStart()

            if (isRoundOver) {
                onEnd()
            } else {
                currentTrick = Trick(currentTrick.winningPlayer, this)
                currentTrick.onStart()
            }
        }

    }

    override fun onEnd() {
        currentTrick.turnOrder.withObservers(game).forEach { player -> player.onRoundEnd(this) }

        game.rounds.add(this)

        if (game.currentTeamOneScore.representableScore >= game.pointsToWin
            || game.currentTeamTwoScore.representableScore >= game.pointsToWin
        ) {
            game.onEnd()
        } else {
            game.currentRound = Round(
                if (game.trace?.handsLeft?.isNotEmpty() == true) game.trace.handsLeft.removeFirst() else currentTrick.turnOrder.generateHands(),
                dealer, game)
        }
    }
}

data class Trick(val starter: Player, @Transient val round: Round) : GameEvent {
    val playedCards: MutableList<Card> = mutableListOf()
    val turnOrder = round.game.playerOrder.getTurnOrder(starter)

    val currentTurn get() = turnOrder[playedCards.size]
    val hasBegun get() = playedCards.isNotEmpty()
    val startingSuit get() = playedCards[0].suit
    val winningCard get() = playedCards.maxByOrNull { it.getValue(this) }!!
    val winningPlayer get() = getPlayerForCard(winningCard)

    fun getPlayedCard(player: Player) = playedCards.getOrNull(turnOrder.indexOf(player))
    fun getPlayerForCard(card: Card) = turnOrder[playedCards.indexOf(card)]

    override fun onStart() {
        turnOrder.withObservers(round.game).forEach { player -> player.onTrickStart(this) }

        while (playedCards.size != 4) {
            val playingPlayer = turnOrder[playedCards.size]
            val playerHand = round.hands.getValue(playingPlayer)
            turnOrder.without(playingPlayer).withObservers(round.game)
                .forEach { player -> player.onOtherTurnRequested(playingPlayer, this) }
            try {
                val card = if (round.game.trace != null && round.game.trace.cardsPlayedLeft.isNotEmpty()) {
                    round.game.trace.cardsPlayedLeft.removeFirst()
                } else playingPlayer.onTurnRequested(playerHand, this)
                playerHand.playCard(card, this)

                turnOrder.withObservers(round.game)
                    .forEach { player -> player.onOtherCardPlayed(card, playingPlayer, this) }
            } catch (e: Exception) {
                e.printStackTrace()
                playingPlayer.onTurnFailed(playerHand, this)
            }
        }

        onEnd()
    }

    override fun onEnd() {
        turnOrder.withObservers(round.game).forEach { player -> player.onTrickEnd(this) }

        round.tricks.add(this)

    }
}

data class Hand(val cards: List<Card>, val player: Player) {
    val playersWithoutParticularSuit: MutableMap<Player, MutableSet<CardSuit>> = mutableMapOf()
    val otherPlayersPlayed: MutableMap<Player, MutableList<Card>> = mutableMapOf()
    val cardsLeft: MutableList<Card> = cards
        .groupBy { it.suit }
        //.toSortedMap(compareByDescending { it == CardSuit.SPADES })
        .map { it.value.sortedByDescending { card -> card.rawCardValue } }
        .flatten()
        .toMutableList()

    fun validateTurn(trick: Trick) = trick.currentTurn == player

    fun getValidCardsForTrick(trick: Trick): List<Card> {
        val cardsInStartingSuit = cardsLeft.filter { if (trick.hasBegun) it.suit == trick.startingSuit else false }
        return if (cardsInStartingSuit.isNotEmpty()) cardsInStartingSuit
        else cardsLeft
    }

    fun playCard(card: Card, trick: Trick) {
        if (trick.currentTurn != player) throw IllegalStateException(card.toString())
        if (card !in cardsLeft) throw IllegalArgumentException(card.toString())
        if (card !in getValidCardsForTrick(trick)) throw IllegalArgumentException(card.toString())
        if (card.suit == CardSuit.SPADES && !trick.round.spadesEnabled) trick.round.spadesEnabled = true
        trick.playedCards.add(card)
        cardsLeft.remove(card)
    }

    fun getComparativeDescendingValueWithCardsLeft(card: Card): Int {
        val cardsLeftInSuit = cardsLeft.filter { it.suit == card.suit }
            .sortedByDescending { it.rawCardValue }
            .mapIndexed { index, c -> index to c }

        return cardsLeftInSuit.first { it.second == card }.first
    }

    override fun toString(): String = "Hand(left=$cardsLeft,leftNum=${cardsLeft.size},player=$player)"

}

data class Card(val suit: CardSuit, val value: CardValue) {
    val rawCardValue = if (suit == CardSuit.SPADES) 13 + value.rawValue else value.rawValue
    fun getValue(trick: Trick): Int {
        return if (suit == CardSuit.SPADES) 13 + value.rawValue
        else {
            if (!trick.hasBegun || trick.startingSuit == suit) value.rawValue
            else 0
        }
    }

    override fun toString(): String = "${value.readable} of ${suit.name.toLowerCase().capitalize()}"
}

enum class CardSuit {
    SPADES,
    CLUBS,
    HEARTS,
    DIAMONDS
}

enum class CardValue(val readable: String, val rawValue: Int) {
    TWO("2", 1),
    THREE("3", 2),
    FOUR("4", 3),
    FIVE("5", 4),
    SIX("6", 5),
    SEVEN("7", 6),
    EIGHT("8", 7),
    NINE("9", 8),
    TEN("10", 9),
    JACK("Jack", 10),
    QUEEN("Queen", 11),
    KING("King", 12),
    ACE("Ace", 13)
}