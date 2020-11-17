package spades.engine

import spades.players.DecisionTreePlayer
import spades.players.RandomPlayer

fun main() {
    val teams =
        (1..2).map { RandomPlayer("RandomPlayer$it") } + (1..2).map { DecisionTreePlayer("DecisionTreePlayer$it") }

     var engine = SpadesEngine()
     engine.teamOne += teams.take(2)
     engine.teamTwo += teams.takeLast(2)

     engine.pointsToWin = 250

     engine.startNewGame()

    val (trace, json) = engine.game.serializeTo(toRound = 3)
    println(json)


    engine = SpadesEngine()
    engine.teamOne += teams.take(2)
    engine.teamTwo += teams.takeLast(2)

    engine.pointsToWin = 250
    engine.traceGame(json)

    val (newTrace, newJson) = engine.game.serialize()

    println(newJson)
    val testAgainst = 3
    println(newTrace.hands.take(testAgainst) == trace.hands.take(testAgainst))
    println(trace.hands.size == 3)

}

