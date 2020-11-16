package spades.players

import spades.models.*

data class ManualPlayer(override val username: String): Player() {
    override fun onOtherCardPlayed(card: Card, player: Player, trick: Trick) {
        println("Card played: $card, by player $player")
    }

    override fun onTrickStart(trick: Trick) {
        println("Trick started")
    }

    override fun onRoundStart() {
        println("Round started")
    }

    override fun onGameStart(game: SpadesGame) {
        println(game)
    }

    override fun onOtherBetMade(bet: Int, player: Player, round: Round) {
        println("Bet made by $player: $bet tricks")
    }

    override fun onTurnRequested(hand: Hand, trick: Trick): Card {
        println(hand)
        println(trick)

        while (true) {
            try {
                println("Please play a card")
                val (cardValueString, _, suitString) = readLine()!!.split(" ")
                return Card(
                    CardSuit.values().first { cardSuit -> cardSuit.name.toLowerCase() == suitString.toLowerCase() },
                    CardValue.values().first { cardValue -> cardValue.readable.toLowerCase() == cardValueString.toLowerCase() }
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onTurnFailed(hand: Hand, trick: Trick) {
        println("Turn failed, please go again")
    }

    override fun onBetRequested(hand: Hand, round: Round): Int? {
        println(round)
        println(hand)
        println("Please enter a bet: ")
        val bet = readLine()!!.toInt()
        return if (bet == 0) null
        else bet
    }

    override fun onOtherTurnRequested(player: Player, trick: Trick) {
        println("Requested $player to go")
    }

    override fun onOtherBetRequested(player: Player, round: Round) {
        println("Requested $player to make a bet")
    }

    override fun onOtherTurnReceived(card: Card, player: Player, trick: Trick) {
        println("Received other turn. $player played $card")
    }

    override fun onBetsFinished(bets: Map<Player, Int>, round: Round) {
        println("Bets made: $bets")
    }

    override fun onTrickEnd(trick: Trick) {
        println("Trick ended.")
    }

    override fun onRoundEnd(round: Round) {
        println("Round ended.")
    }

    override fun onGameEnd(game: SpadesGame) {
        println("Game ended.")
    }

}