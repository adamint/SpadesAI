package spades.players

import spades.models.*

abstract class GameObserver(override val username: String) : Player()

class TotalGameObserver(override val username: String): GameObserver(username) {
    override fun onTrickStart(trick: Trick) {
        println("Trick start")
    }

    override fun onRoundStart() {
        println("Round start")
    }

    override fun onGameStart(game: SpadesGame) {
        println("Game start")
    }

    override fun onTrickEnd(trick: Trick) {
        println("Trick end")
    }

    override fun onRoundEnd(round: Round) {
        println("Round end")
        println("Team 1 bet: ${round.roundTeamOneScore.teamBets} and made: ${round.roundTeamOneScore.teamMade}, for ${round.roundTeamOneScore.pointsMade} points (${round.roundTeamOneScore.pointsAfter})")
        println("Team 2 bet: ${round.roundTeamTwoScore.teamBets} and made: ${round.roundTeamTwoScore.teamMade}, for ${round.roundTeamTwoScore.pointsMade} points (${round.roundTeamTwoScore.pointsAfter})")
        println("tricks: ${round.getTricksWonByPlayer().map { it.key to it.value.size }}")
    }

    override fun onGameEnd(game: SpadesGame) {
        println("Game end")
        println("Game team one scoring: ${game.currentTeamOneScore.representableScore}")
        println("Game team two scoring: ${game.currentTeamTwoScore.representableScore}")
    }

    override fun onOtherCardPlayed(card: Card, player: Player, trick: Trick) {
        println("$player played card $card")
    }

    override fun onOtherBetMade(bet: Int, player: Player, round: Round) {
    }

    override fun onTurnRequested(hand: Hand, trick: Trick): Card {
        throw NotImplementedError()
    }

    override fun onTurnFailed(hand: Hand, trick: Trick) {
        throw NotImplementedError()
    }

    override fun onBetRequested(hand: Hand, round: Round): Int? {
        throw NotImplementedError()
    }

    override fun onOtherTurnRequested(player: Player, trick: Trick) {
        println("$player turn requested")
    }

    override fun onOtherBetRequested(player: Player, round: Round) {
        println("$player bet requested")
    }

    override fun onOtherTurnReceived(card: Card, player: Player, trick: Trick) {
        println("$player played card $card in trick")
    }

    override fun onBetsFinished(bets: Map<Player, Int>, round: Round) {
        println("All bets for round received: $bets")
    }
}