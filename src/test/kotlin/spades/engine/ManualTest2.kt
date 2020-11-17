package spades.engine

import spades.players.*

fun main() {
   /* val manualPlayer = ManualPlayer("Manual")
    val randoms = (1..3).map { RandomPlayer("Random$it") }

    val engine = SpadesEngine()
    engine.teamOne += listOf(manualPlayer, randoms[0])
    engine.teamTwo += listOf(randoms[1], randoms[2])

    engine.pointsToWin = 250

    engine.startGame()*/

    val teams = listOf(
        (1..2).map { DecisionTreePlayer("DecisionTreePlayer$it") } + (1..2).map { BasicHeuristicPlayer("HeuristicPlayer$it") },
        (1..2).map { RandomPlayer("RandomPlayer$it") } + (1..2).map { BasicHeuristicPlayer("HeuristicPlayer$it") },
            (1..2).map { RandomPlayer("RandomPlayer$it") } + (1..2).map { DecisionTreePlayer("DecisionTreePlayer$it") }

    )

    teams.forEach { team ->
        var teamOneWins = 0

        val repeats = 100
        for (i in 1..repeats) {
            val engine = SpadesEngine()
            engine.teamOne += listOf(team[0], team[1])
            engine.teamTwo += listOf(team[2], team[3])
            engine.pointsToWin = 250

            println("Team 1 (${engine.teamOne}) facing Team 2 (${engine.teamTwo})")

            //engine.observers += TotalGameObserver("TGO1")
            engine.startNewGame()
            println("Team 1 score: ${engine.game.currentTeamOneScore.representableScore}")
            println("Team 2 score: ${engine.game.currentTeamTwoScore.representableScore}")

            if (engine.game.winner == engine.teamOne) teamOneWins++
        }

        println("Team 1 (${team.take(2)}) won ${100 * teamOneWins.toDouble() / repeats}% of the time against Team 2 (${team.takeLast(2)})")
    }

}

