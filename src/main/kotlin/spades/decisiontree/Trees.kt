package spades.decisiontree

import hu.webarticum.treeprinter.TraditionalTreePrinter
import org.apache.commons.math3.distribution.NormalDistribution
import spades.engine.Card
import spades.engine.Hand
import spades.engine.Round
import spades.engine.Trick
import spades.players.HeuristicPlayer
import spades.utils.sum

val isTeammateLikelyToWin = IsTeammateLikelyToWin()

val haveTotalTeamTricksBeenObtained = HaveTotalTeamTricksBeenObtained()
val isTeammateLeadingTrick = IsTeammateLeadingTrick()

val isLeadingTrick = IsLeadingTrick()

val decisionTreeRoot = DecisionTreeRoot()


class DecisionTreeRoot : DecisionNode<Card>(children = listOf(isLeadingTrick)) {
    override fun classifyImpl(hand: Hand, trick: Trick, round: Round): Card {
        return isLeadingTrick.classify(hand, trick, round)
    }
}

class IsLeadingTrick : DecisionNode<Card>(children = listOf(haveTotalTeamTricksBeenObtained, isTeammateLeadingTrick)) {
    override fun classifyImpl(hand: Hand, trick: Trick, round: Round): Card {
        return if (!trick.hasBegun) haveTotalTeamTricksBeenObtained.classify(hand, trick, round)
        else isTeammateLeadingTrick.classify(hand, trick, round)
    }
}

class HaveTotalTeamTricksBeenObtained : DecisionNode<Card>(children = listOf(maximizeLoss, maximizeGain)) {
    override fun classifyImpl(hand: Hand, trick: Trick, round: Round): Card {
        val bets = round.teamOneBet.sum()
        val made = round.teamOneMade.sum()

        return if (made >= bets) maximizeLoss.classify(hand, trick, round)
        else maximizeGain.classify(hand, trick, round)
    }
}


class IsTeammateLeadingTrick : DecisionNode<Card>(children = listOf(isTeammateLikelyToWin, maximizeGain)) {
    override fun classifyImpl(hand: Hand, trick: Trick, round: Round): Card {
        return if (!trick.hasBegun || trick.winningPlayer.partner != hand.player) maximizeGain.classify(
            hand,
            trick,
            round
        )
        else isTeammateLikelyToWin.classify(hand, trick, round)
    }
}

class IsTeammateLikelyToWin : DecisionNode<Card>(children = listOf(maximizeGain, maximizeLoss, minimizeLoss)) {
    override fun classifyImpl(hand: Hand, trick: Trick, round: Round): Card {
        if (round.teamOneMade.sum() >= round.teamOneBet.sum()) return maximizeLoss.classify(hand, trick, round)

        val aggressiveness = hand.player.aggressiveness

        val myTurnIndex = trick.turnOrder.indexOf(hand.player)

        if (myTurnIndex == 3) {
            return if (trick.winningPlayer == hand.player.partner) minimizeLoss.classify(hand, trick, round)
            else maximizeGain.classify(hand, trick, round)
        }

        val remainingPlayer = trick.turnOrder[myTurnIndex + 1]
        val remainingPlayerTeamBet =
            listOf(remainingPlayer, remainingPlayer.partner).map { round.getBets().getValue(it) }
        val remainingPlayerTeamTricksMade =
            listOf(remainingPlayer, remainingPlayer.partner).map { round.getTricksWonByPlayer().getValue(it).size }

        val remainingPlayerTeamNeedsTricks = remainingPlayerTeamTricksMade.sum() < remainingPlayerTeamBet.sum()
        val remainingPlayerTrickDeficit = remainingPlayerTeamTricksMade[0] - remainingPlayerTeamBet[0]

        var likelihoodOfLoss = 0.0

        if (trick.startingSuit in hand.playersWithoutParticularSuit.getOrDefault(remainingPlayer, mutableSetOf())) {
            likelihoodOfLoss += if (remainingPlayerTeamNeedsTricks && remainingPlayerTrickDeficit < 0) 0.6
            else if (remainingPlayerTeamNeedsTricks) 0.3
            else 0.1
        } else {
            val numCardsInSuitPlayed =
                hand.otherPlayersPlayed.getOrDefault(remainingPlayer, mutableListOf()).filter { it.suit == trick.startingSuit }.size
            val probabilityOtherPlayerHasSuit =
                NormalDistribution(3.25, 1.0).cumulativeProbability(numCardsInSuitPlayed.toDouble())
            val numCardsBeatingThatWeDontHave =
                (hand.player as HeuristicPlayer).cardsLeftInDeck.getValue(trick.startingSuit)
                    .filter { it.rawCardValue > trick.winningCard.rawCardValue && it !in hand.cardsLeft }.size

            val cardsLeftInSuit = 13 - numCardsInSuitPlayed
            val probabilityHasACard =
                numCardsBeatingThatWeDontHave.toDouble() / cardsLeftInSuit.toDouble() / 3.0 + 2 *
                        (1.0 - probabilityOtherPlayerHasSuit)

            likelihoodOfLoss += probabilityHasACard
        }

        return if (aggressiveness > (1 - likelihoodOfLoss/2.0)) maximizeGain.classify(hand, trick, round)
        else minimizeLoss.classify(hand, trick, round)
    }
}


fun <T> DecisionNode<T>.getAllWithChildren(): List<DecisionNode<T>> {
    return when {
        children.isEmpty() -> listOf(this)
        else -> children.map { it.getAllWithChildren() }.flatten() + this
    }
}

fun main() {
    //println(NormalDistribution(3.25, 1.0).cumulativeProbability(5.0))
    TraditionalTreePrinter().print((decisionTreeRoot.getPrintableNode()))
    decisionTreeRoot.getAllWithChildren()
        .filter { it.readableName != it::class.simpleName }
        .distinct()
        .map { "${it.readableName} = ${it::class.simpleName}" }
        .forEach { println(it) }
}
