package spades.engine

import spades.players.Player
import spades.models.SpadesGame
import spades.players.GameObserver
import spades.utils.generateRandomPlayerOrder

class SpadesEngine {
    val teamOne: MutableList<Player> = mutableListOf()
    val teamTwo: MutableList<Player> = mutableListOf()

    val observers: MutableList<GameObserver> = mutableListOf()

    lateinit var playerOrdering: List<Player>
    lateinit var game: SpadesGame

    var pointsToWin: Int = 250

    fun addPlayer(player: Player, isTeamOne: Boolean) {
        val team = if (isTeamOne) teamOne else teamTwo
        if (team.size == 2) throw IllegalArgumentException()
        team.add(player)

        if (teamOne.size == 2 && teamTwo.size == 2) startGame()
    }

    fun traceGame() {
        game = SpadesGame(
            teamOne,
            teamTwo,
            playerOrdering,
            observers,
            pointsToWin
        )
    }

    fun startGame() {
        if (!this::playerOrdering.isInitialized) playerOrdering = generateRandomPlayerOrder(teamOne, teamTwo)

        game = SpadesGame(
            teamOne,
            teamTwo,
            playerOrdering,
            observers,
            pointsToWin
        )

        game.onStart()
    }
}