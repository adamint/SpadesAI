package spades.players

import spades.engine.*
import spades.utils.allCards
import kotlin.math.min

abstract class HeuristicPlayer(
    override val username: String,
    aggressiveness: Double = 0.5,
    matchPartnerAggressiveness: Boolean = true
) : Player(aggressiveness, matchPartnerAggressiveness) {
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
            suitValues.add(playerCard to suit.indexOf(playerCard) + 1)
        }

        return suitValues
    }

    override fun onBetRequested(hand: Hand, round: Round): Int? {
        var expectedMade = 0.0
        // group hand by suit

        // each suit's bet factor:
        // sum(card bet factor) + trump play likelihood
        // trump play likelihood = if num trump == 1 and cards in suit <= 2 then 1
        // else if num trump == 2 and cards in suit <= 2 then 1.5
        // else if num trump

        // (num in suit excluding our cards)/(total in suit excluding our cards)
        // if not trump +


        hand.cards
            .groupBy { it.suit }
            .forEach { (suit, cards) ->
                var suitExpected = 0.0
                val totalSuitForOthers = 13 - cards.size
                val topCards = cards.sortedByDescending { it.rawCardValue }
                // if suit is not spades, we take max top 3 cards
                if (suit != CardSuit.SPADES) {
                    topCards.filter { it.value.rawValue >= CardValue.JACK.rawValue }.forEach { card ->
                        suitExpected += (card.rawCardValue - cards.size).toDouble() / totalSuitForOthers
                    }
                } else {
                    // else we take all cards and apply the same thing
                    topCards.forEach { card ->
                        suitExpected += (card.value.rawValue - cards.size).toDouble() / totalSuitForOthers
                    }
                }

                val numTrump = hand.cards.filter { it.suit == CardSuit.SPADES }.size
                if (cards.size <= 2 && numTrump >= 2) {
                    (1..numTrump).forEach { suitExpected += 1.0 / it.toDouble() }
                }

                expectedMade += min(2.0, suitExpected)
                // println("Expecting $suitExpected from $suit")
            }


        var expectedMadeRoundedDown = (expectedMade.toInt() - 2.0 * (1.0 - aggressiveness / 2.0)).toInt()
        if (expectedMadeRoundedDown < 1) expectedMadeRoundedDown = 1
        // println("Betting for $username: $expectedMadeRoundedDown")
        return if (expectedMadeRoundedDown == 0) null
        else expectedMadeRoundedDown
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
        if (matchPartnerAggressiveness) {
            val bet = round.getBets().getValue(partner).toDouble()
            val made = round.getTricksWonByPlayer().getValue(partner).size.toDouble()

            aggressiveness =
                if (bet == made) 0.5
                else 1.0 - (made / bet) / 2.0
        }
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