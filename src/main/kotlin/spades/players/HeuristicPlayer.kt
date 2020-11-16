package spades.players

import spades.models.*
import spades.utils.allCards

abstract class HeuristicPlayer(override val username: String) : Player() {
    val cardsLeftInDeck by lazy {
        val cards = allCards.toMutableList()

        cards
            .groupBy { it.suit }
            .map { it.key to it.value.toMutableList() }
            .toMap()
            .toMutableMap()
    }

    fun getCardsLeftInSuit(suit: CardSuit) = cardsLeftInDeck[suit]!!.size

    fun getHandByRemainingSuitValue(): List<Pair<Card, Int>> {
        val suitValues = mutableListOf<Pair<Card, Int>>()
        hand.cardsLeft.forEach { playerCard ->
            val suit = cardsLeftInDeck.getValue(playerCard.suit).sortedByDescending { it.value.rawValue }
            suitValues.add(playerCard to suit.indexOf(playerCard)+ 1)
        }

        return suitValues
    }

    override fun onTrickStart(trick: Trick) {
    }

    override fun onRoundStart() {
    }

    override fun onGameStart(game: SpadesGame) {
    }

    override fun onTrickEnd(trick: Trick) {
    }

    override fun onRoundEnd(round: Round) {
    }

    override fun onGameEnd(game: SpadesGame) {
    }

    override fun onOtherCardPlayed(card: Card, player: Player, trick: Trick) {
        cardsLeftInDeck[card.suit]!!.remove(card)
        super.hand.otherPlayersPlayed.putIfAbsent(player, mutableListOf())
        super.hand.otherPlayersPlayed.getValue(player).add(card)

        if (trick.playedCards.size > 1 && trick.startingSuit != card.suit) {
            super.hand.playersWithoutParticularSuit.putIfAbsent(player, mutableSetOf())
            super.hand.playersWithoutParticularSuit.getValue(player).add(card.suit)
        }
    }

    override fun onOtherBetMade(bet: Int, player: Player, round: Round) {
    }

    override fun onTurnFailed(hand: Hand, trick: Trick) {
    }

    override fun onOtherTurnRequested(player: Player, trick: Trick) {
    }

    override fun onOtherBetRequested(player: Player, round: Round) {
    }

    override fun onOtherTurnReceived(card: Card, player: Player, trick: Trick) {
    }


    override fun onBetsFinished(bets: Map<Player, Int>, round: Round) {
    }
}