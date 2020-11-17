package spades.engine

import com.google.gson.Gson
import spades.players.GameObserver
import spades.players.Player
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

        if (teamOne.size == 2 && teamTwo.size == 2) startNewGame()
    }

    fun traceGame(traceString: String) {
        val trace = Gson().fromJson(traceString, Trace::class.java)
        startNewGame(Trace(trace.cardsPlayed, trace.hands, trace.startingDealerUsername, trace.playerOrderUsernames))
    }

    fun startNewGame(trace: Trace? = null) {
        val playerOrderingString = if (!this::playerOrdering.isInitialized) {
            trace?.playerOrderUsernames ?: generateRandomPlayerOrder(teamOne, teamTwo).map { it.username }
        } else playerOrdering.map { it.username }

        game = SpadesGame(
            teamOne,
            teamTwo,
            playerOrderingString,
            observers,
            pointsToWin,
            trace
        )

        trace?.let { it.game = game }

        game.onStart()
    }
}