package spades.engine

import com.google.gson.Gson
import spades.players.GameObserver
import spades.players.Player
import spades.utils.generateHands
import spades.utils.withObservers

data class Trace(
    val cardsPlayed: List<Card>,
    val hands: List<Map<String, List<Card>>>,
    val startingDealerUsername: String,
    val playerOrderUsernames: List<String>
) {
    @Transient
    lateinit var game: SpadesGame

    @Transient
    val cardsPlayedLeft = ArrayDeque(cardsPlayed)

    @delegate:Transient
    val handsLeft by lazy {
        ArrayDeque(hands.map { round ->
            round.toList().map {
                val player = (game.teamOne + game.teamTwo).first { player -> player.username == it.first }
                player to Hand(
                    it.second,
                    player
                )
            }.toMap()
        })
    }

    @delegate:Transient
    val startingDealer by lazy {
        (game.teamOne + game.teamTwo).first { player -> player.username == startingDealerUsername }
    }
}

data class SpadesGame(
    val teamOne: List<Player>,
    val teamTwo: List<Player>,
    val playerOrderUsernames: List<String>,
    val observers: List<GameObserver>,
    val pointsToWin: Int,
    val trace: Trace? = null
) : GameEvent {
    val rounds: MutableList<Round> = mutableListOf<Round>().apply {
        trace?.game = this@SpadesGame
    }

    val playerOrder: List<Player> = playerOrderUsernames.map { ou -> (teamOne + teamTwo).first { it.username == ou } }

    var currentRound: Round = Round(
        if (trace?.handsLeft?.isNotEmpty() == true) trace.handsLeft.removeFirst() else playerOrder.generateHands(),
        trace?.startingDealer ?: playerOrder.random(),
        this
    )

    val currentTeamOneScore get() = rounds.last().roundTeamOneScore
    val currentTeamTwoScore get() = rounds.last().roundTeamTwoScore

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
        println("Over after $i rounds, with winners $winner")
    }

    override fun onEnd() {
        playerOrder.withObservers(this).forEach { player -> player.onGameEnd(this) }
    }

    override fun toString(): String {
        return serializeTo(rounds.size + 1).second
    }

    fun serialize() = serializeTo(rounds.size + 1)

    fun serializeTo(toRound: Int): Pair<Trace, String> {
        val cards = (if (currentRound in rounds) rounds else rounds + currentRound).asSequence().take(toRound)
            .map { round ->
                (if (round.currentTrick in round.tricks) round.tricks else round.tricks + round.currentTrick)
                    .distinctBy { it.playedCards }
            }
            .flatten()
            .map { it.playedCards }
            .flatten().toList()


        val hands = (if (currentRound in rounds) rounds else (rounds + currentRound)).take(toRound)
            .map { round -> round.hands.toList().map { it.first.username to it.second.cards }.toMap() }

        val trace = Trace(cards, hands, rounds.first().dealer.username, playerOrderUsernames)
        return trace to Gson().toJson(trace)
    }
}

interface GameEvent {
    fun onStart()
    fun onEnd()
}