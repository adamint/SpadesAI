package spades.models

import spades.players.GameObserver
import spades.players.Player
import spades.utils.generateHands
import spades.utils.withObservers
import java.util.*

data class SpadesGame(
    val teamOne: List<Player>,
    val teamTwo: List<Player>,
    val playerOrder: List<Player>,
    val observers: List<GameObserver>,
    val pointsToWin: Int,
    val trace: List<Card>? = null
) : GameEvent {
    val rounds: MutableList<Round> = mutableListOf()

    var currentRound: Round = Round(playerOrder.generateHands(), playerOrder.random(), this)

    val currentTeamOneScore get() = rounds.last().roundTeamOneScore
    val currentTeamTwoScore get() = rounds.last().roundTeamTwoScore

    val traceCardsLeft = trace?.let { PriorityQueue(it) }

    val isGameOver
        get() =
            if (rounds.isEmpty()) false
            else currentTeamOneScore.representableScore >= pointsToWin
                    || currentTeamTwoScore.representableScore >= pointsToWin

    val winner
        get() = when {
            currentTeamOneScore.representableScore >= pointsToWin -> teamOne
            currentTeamTwoScore.representableScore >= pointsToWin -> teamTwo
            else -> null
        }

    override fun onStart() {
        teamOne[0].partner = teamOne[1]
        teamOne[1].partner = teamOne[0]
        teamTwo[0].partner = teamTwo[1]
        teamTwo[1].partner = teamTwo[0]

        playerOrder.withObservers(this).forEach { player -> player.onGameStart(this) }
        var i = 0
        while (!isGameOver) {
            i++
            currentRound.onStart()
        }
        println("over after $i rounds")
    }

    override fun onEnd() {
        playerOrder.withObservers(this).forEach { player -> player.onGameEnd(this) }
    }
}

interface GameEvent {
    fun onStart()
    fun onEnd()
}