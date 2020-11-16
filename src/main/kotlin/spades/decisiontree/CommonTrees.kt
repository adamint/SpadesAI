package spades.decisiontree

import spades.models.*

// loss minimization
val minimizeLossInSuit = MinimizeLossInSuit()
val minimizeLossOutOfSuit = MinimizeLossOutOfSuit()
val minimizeLoss = MinimizeLoss()

// loss maximization (and gain minimization)
val maximizeLossInSuit = MaximizeLossInSuit()
val maximizeSpadesLoss = MaximizeSpadesLoss()
val maximizeLossOutOfSuit = MaximizeLossOutOfSuit()
val maximizeLoss = MaximizeLoss()


// gain maximization
val playHigherSpadesCardOutOfSuit = PlayHigherSpadesCardOutOfSuit()
val playCardToMaximizeGainInSuit = PlayCardToMaximizeGainInSuit()
val maximizeGainOutOfSuit = MaximizeGainOutOfSuit()
val maximizeGainInSuit = MaximizeGainInSuit()

val maximizeGainLeadingTrick = MaximizeGainLeadingTrick()
val maximizeGainNotLeadingTrick = MaximizeGainNotLeadingTrick()
val maximizeGain = MaximizeGain()
///

class MinimizeLoss : DecisionNode<Card>(children = listOf(minimizeLossInSuit, minimizeLossOutOfSuit)) {
    override fun classifyImpl(hand: Hand, trick: Trick, round: Round): Card {
        return if (trick.startingSuit in hand.cardsLeft.map { it.suit }) {
            minimizeLossInSuit.classify(hand, trick, round)
        } else minimizeLossOutOfSuit.classify(hand, trick, round)
    }
}

class MinimizeLossInSuit : DecisionNode<Card>() {
    override fun classifyImpl(hand: Hand, trick: Trick, round: Round): Card {
        return hand.cardsLeft.filter { it.suit == trick.startingSuit }
            .minByOrNull { it.rawCardValue }!!
    }
}

class MinimizeLossOutOfSuit : DecisionNode<Card>() {
    override fun classifyImpl(hand: Hand, trick: Trick, round: Round): Card {
        val nonSuitCards = hand.cardsLeft.filter { it.suit != trick.startingSuit }
            .map { it to hand.getComparativeDescendingValueWithCardsLeft(it) }

        return nonSuitCards.maxByOrNull { it.second }!!.first
    }
}

///

class MaximizeLoss :
    DecisionNode<Card>(children = listOf(maximizeLossInSuit, maximizeLossOutOfSuit, maximizeSpadesLoss)) {
    override fun classifyImpl(hand: Hand, trick: Trick, round: Round): Card {
        if (!trick.hasBegun) return maximizeGainLeadingTrick.classify(hand, trick, round)
        return if (trick.startingSuit in hand.cardsLeft.map { it.suit }) {
            maximizeLossInSuit.classify(hand, trick, round)
        } else if (CardSuit.SPADES in trick.playedCards.map { it.suit } && CardSuit.SPADES in hand.cardsLeft.map { it.suit }) {
            maximizeSpadesLoss.classify(hand, trick, round)
        } else maximizeLossOutOfSuit.classify(hand, trick, round)
    }
}

class MaximizeLossInSuit : DecisionNode<Card>() {
    override fun classifyImpl(hand: Hand, trick: Trick, round: Round): Card {
        val cardsInSuit = hand.cardsLeft.filter { it.suit == trick.startingSuit }
            .map { it to hand.getComparativeDescendingValueWithCardsLeft(it) }
            .sortedBy { it.second }

        val highestNonWinningCard = cardsInSuit.firstOrNull { it.first.rawCardValue < trick.winningCard.rawCardValue }

        return (highestNonWinningCard ?: cardsInSuit.first()).first
    }
}

class MaximizeSpadesLoss : DecisionNode<Card>() {
    override fun classifyImpl(hand: Hand, trick: Trick, round: Round): Card {
        val cardsInSpades = hand.cardsLeft.filter { it.suit == CardSuit.SPADES }
            .map { it to hand.getComparativeDescendingValueWithCardsLeft(it) } // lower means better
            .sortedBy { it.second }

        val highestNonWinningCard = cardsInSpades.firstOrNull { it.first.rawCardValue < trick.winningCard.rawCardValue }

        return (highestNonWinningCard ?: cardsInSpades.first()).first
    }
}

class MaximizeLossOutOfSuit : DecisionNode<Card>() {
    override fun classifyImpl(hand: Hand, trick: Trick, round: Round): Card {
        val cardsOutOfSuit = hand.cardsLeft.filter { it.suit != trick.startingSuit }
            .map { it to hand.getComparativeDescendingValueWithCardsLeft(it) } // lower is better
            .sortedBy { it.second }

        val highestNonWinningCard = cardsOutOfSuit.firstOrNull { it.first.suit != CardSuit.SPADES }

        return (highestNonWinningCard ?: cardsOutOfSuit.first()).first
    }
}

class MaximizeGain : DecisionNode<Card>(children = listOf(maximizeGainLeadingTrick, maximizeGainNotLeadingTrick, maximizeLoss)) {
    override fun classifyImpl(hand: Hand, trick: Trick, round: Round): Card {
        val playerTeamBet =
            listOf(hand.player, hand.player.partner).map { round.getBets().getValue(it) }.sum()
        val playerTeamTricksMade =
            listOf(hand.player, hand.player.partner.partner).map { round.getTricksWonByPlayer().getValue(it).size }.sum()

        return when {
            trick.hasBegun && playerTeamTricksMade >= playerTeamBet -> maximizeLoss.classify(hand, trick, round)
            trick.hasBegun -> maximizeGainNotLeadingTrick.classify(hand, trick, round)
            else -> maximizeGainLeadingTrick.classify(hand, trick, round)
        }
    }
}

class MaximizeGainLeadingTrick : DecisionNode<Card>() {
    override fun classifyImpl(hand: Hand, trick: Trick, round: Round): Card {
        val nonSpadesCards = hand.cardsLeft.filter { it.suit != CardSuit.SPADES }
            .map { it to hand.getComparativeDescendingValueWithCardsLeft(it) } // lower is better
            .sortedBy { it.second }

        return nonSpadesCards.firstOrNull()?.first
            ?: hand.cardsLeft.minByOrNull { it.rawCardValue }!! // only spades left
    }
}

class MaximizeGainNotLeadingTrick : DecisionNode<Card>(children = listOf(maximizeGainInSuit, maximizeGainOutOfSuit)) {
    override fun classifyImpl(hand: Hand, trick: Trick, round: Round): Card {
        return if (trick.startingSuit in hand.cardsLeft.map { it.suit }) maximizeGainInSuit.classify(hand, trick, round)
        else maximizeGainOutOfSuit.classify(hand, trick, round)
    }
}

class MaximizeGainInSuit : DecisionNode<Card>(children = listOf(minimizeLoss, playCardToMaximizeGainInSuit)) {
    override fun classifyImpl(hand: Hand, trick: Trick, round: Round): Card {
        if (trick.startingSuit != CardSuit.SPADES && trick.winningCard.suit == CardSuit.SPADES) {
            return minimizeLoss.classify(hand, trick, round)
        }
        val leadingCards = hand.cardsLeft
            .filter { it.suit == trick.startingSuit }
            .filter { it.rawCardValue > trick.winningCard.rawCardValue }
            .sortedByDescending { it.rawCardValue }

        return if (leadingCards.isEmpty()) minimizeLoss.classify(hand, trick, round)
        else playCardToMaximizeGainInSuit.classify(hand, trick, round)
    }
}

class PlayCardToMaximizeGainInSuit : DecisionNode<Card>() {
    override fun classifyImpl(hand: Hand, trick: Trick, round: Round): Card {
        val leadingCards = hand.cardsLeft
            .filter { it.suit == trick.startingSuit }
            .filter { it.rawCardValue > trick.winningCard.rawCardValue }
            .sortedByDescending { it.rawCardValue }

        return leadingCards.first()
    }
}


class MaximizeGainOutOfSuit : DecisionNode<Card>(children = listOf(minimizeLoss, playHigherSpadesCardOutOfSuit)) {
    override fun classifyImpl(hand: Hand, trick: Trick, round: Round): Card {
            val spadesInHand = hand.cardsLeft.filter { it.suit == CardSuit.SPADES }
            return if (spadesInHand.isEmpty() || spadesInHand.maxOf { it.value } < trick.winningCard.value) minimizeLoss.classify(hand, trick, round)
            else playHigherSpadesCardOutOfSuit.classify(hand, trick, round)

    }
}

class PlayHigherSpadesCardOutOfSuit : DecisionNode<Card>() {
    override fun classifyImpl(hand: Hand, trick: Trick, round: Round): Card {
        val needToBeat = if (trick.winningCard.suit == CardSuit.SPADES) trick.winningCard.rawCardValue else -1

        val spadesInHand = hand.cardsLeft
            .filter { it.suit == CardSuit.SPADES && it.rawCardValue > needToBeat }
            .sortedByDescending { it.rawCardValue}

        return spadesInHand.random()
    }
}