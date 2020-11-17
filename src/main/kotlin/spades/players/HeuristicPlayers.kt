package spades.players

import spades.decisiontree.decisionTreeRoot
import spades.engine.Card
import spades.engine.CardSuit
import spades.engine.Hand
import spades.engine.Trick

class RandomPlayer(username: String) : HeuristicPlayer(username) {
    override fun onTurnRequested(hand: Hand, trick: Trick): Card {
        return hand.getValidCardsForTrick(trick).random()
    }
}

open class BasicHeuristicPlayer(
    username: String,
    aggressiveness: Double = 0.5,
    matchPartnerAggressiveness: Boolean = true
) : HeuristicPlayer(username, aggressiveness, matchPartnerAggressiveness) {
    override fun onTurnRequested(hand: Hand, trick: Trick): Card {
        val round = trick.round
        val cardValues = getHandByRemainingSuitValue()
            .sortedBy { it.second }

        return if (trick.playedCards.isEmpty()) {
            // if starting
            val topCard =
                (if (round.spadesEnabled || cardValues.none { it.first.suit != CardSuit.SPADES }) cardValues
                else cardValues.filter { it.first.suit != CardSuit.SPADES })
                    .sortedByDescending { if (cardsLeftInDeck.getValue(it.first.suit).size > 5) 1 else 0 }.take(3)
                    .random()

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
                < round.getBets().getValue(this) + round.getBets().getValue(this.partner)
            ) {
                cardValues.last().first
            } // if hasn't gotten target amount of tricks in round, play lowest card
            else cardValues.first().first // else play highest card
        }
    }
}

class DecisionTreePlayer(
    username: String,
    aggressiveness: Double = 0.5,
    matchPartnerAggressiveness: Boolean = true
) : BasicHeuristicPlayer(username, aggressiveness, matchPartnerAggressiveness) {
    val root = decisionTreeRoot
    override fun onTurnRequested(hand: Hand, trick: Trick): Card {
        return root.classify(hand, trick, trick.round)
    }
}