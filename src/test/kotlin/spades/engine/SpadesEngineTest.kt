package spades.engine

import org.junit.Test
import spades.players.ManualPlayer
import spades.players.RandomPlayer

class SpadesEngineTest {
    @Test
    fun startGame() {
        val manualPlayer = ManualPlayer("Manual")
        val randoms = (1..3).map { RandomPlayer("Random$it") }

        val engine = SpadesEngine()
        engine.teamOne += listOf(manualPlayer, randoms[0])
        engine.teamTwo += listOf(randoms[1], randoms[2])

        engine.pointsToWin = 250

        engine.startGame()
    }
}