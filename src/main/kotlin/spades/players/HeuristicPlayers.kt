package spades.players

import spades.decisiontree.decisionTreeRoot
import spades.models.*
import kotlin.math.min
import kotlin.random.Random

class RandomPlayer(override val username: String) : HeuristicPlayer(username) {
    override fun onTurnRequested(hand: Hand, trick: Trick): Card {
        return hand.getValidCardsForTrick(trick).random()
    }

    override fun onBetRequested(hand: Hand, round: Round): Int? {
        val betsGiven = round.bets.sum()
        return Random.nextInt(0, min(Random.nextInt(5) + 1, 13 - betsGiven) + 1)
    }
}

open class BasicHeuristicPlayer(override val username: String) : HeuristicPlayer(username) {
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
                println("Expecting $suitExpected from $suit")
            }


        var expectedMadeRoundedDown = expectedMade.toInt() - 2
        if (expectedMadeRoundedDown < 1) expectedMadeRoundedDown = 1
        println("Betting for $username: $expectedMadeRoundedDown")
        return if (expectedMadeRoundedDown == 0) null
        else expectedMadeRoundedDown
    }

    override fun onTurnRequested(hand: Hand, trick: Trick): Card {
        val round = trick.round
        val cardValues = getHandByRemainingSuitValue()
            .sortedBy { it.second }

        return if (trick.playedCards.isEmpty()) {
            // if starting
            val topCard =
                (if (round.spadesEnabled || cardValues.none { it.first.suit != CardSuit.SPADES }) cardValues
                else cardValues.filter { it.first.suit != CardSuit.SPADES })
                    .sortedByDescending { if (cardsLeftInDeck.getValue(it.first.suit).size > 5) 1 else 0 }.take(3).random()

            topCard.first
        } else {
            // if not leading
            val currentWinningCard = trick.winningCard

            // if we have a higher card in starting suit AND we have not obtained goal tricks play it
            // else if we have a card in starting suit play lowest
            // else if spades is enabled and we have not obtained goal tricks play lowest spade
            // else play lowest card
            val cardValuesInSuit = cardValues
                .filter { it.first.suit == trick.startingSuit }
            val cardValuesHigherInSuit = cardValuesInSuit
                .filter { it.first.rawCardValue > currentWinningCard.rawCardValue }

            // if has suit and can beat suit play lowest higher card in suit
            if (cardValuesInSuit.isNotEmpty()
                && cardValuesHigherInSuit.isNotEmpty()
                && round.getTricksWonByPlayer()[this]!!.size + round.getTricksWonByPlayer()[this.partner]!!.size
                < round.getBets().getValue(this) + round.getBets().getValue(this.partner)
            ) {
                cardValuesHigherInSuit.last().first
            } else if (cardValuesInSuit.isNotEmpty()) cardValuesInSuit.random().first // if has suit but can't win play lowest in suit
            else if (round.getTricksWonByPlayer()[this]!!.size + round.getTricksWonByPlayer()[this.partner]!!.size
                < round.getBets().getValue(this) + round.getBets().getValue(this.partner)
                && cardValues.any { it.first.suit == CardSuit.SPADES } // if has spade and can win
            ) {
                cardValues.last { it.first.suit == CardSuit.SPADES }.first
            } else if (round.getTricksWonByPlayer()[this]!!.size + round.getTricksWonByPlayer()[this.partner]!!.size
                < round.getBets().getValue(this) + round.getBets().getValue(this.partner)) {
                cardValues.last().first
            } // if hasn't gotten target amount of tricks in round, play lowest card
            else cardValues.first().first // else play highest card
        }
    }
}

class DecisionTreePlayer(override val username: String) : BasicHeuristicPlayer(username) {
    val root = decisionTreeRoot
    override fun onTurnRequested(hand: Hand, trick: Trick): Card {
        return root.classify(hand, trick, trick.round)
    }
}