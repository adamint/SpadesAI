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

    var teamOneWins = 0

    val repeats = 250
    for (i in 1..repeats) {
        val randoms =
            (1..2).map { DecisionTreePlayer("DecisionTreePlayer$it") } + (1..2).map { BasicHeuristicPlayer("HeuristicPlayer$it") }
        val engine = SpadesEngine()
        engine.teamOne += listOf(randoms[0], randoms[1])
        engine.teamTwo += listOf(randoms[2], randoms[3])
        engine.pointsToWin = 250
        engine.observers += TotalGameObserver("TGO1")
        engine.startGame()
        if (engine.game.winner == engine.teamOne) teamOneWins++
    }

    println("Decision tree team won ${100 * teamOneWins.toDouble() / repeats}% of the time")

}

/*



 */