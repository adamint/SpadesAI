package spades.players

import spades.engine.*
import kotlin.properties.Delegates

abstract class Player {
    abstract val username: String
    abstract fun onTrickStart(trick: Trick)
    abstract fun onRoundStart()
    abstract fun onGameStart(game: SpadesGame)

    abstract fun onTrickEnd(trick: Trick)
    abstract fun onRoundEnd(round: Round)
    abstract fun onGameEnd(game: SpadesGame)

    abstract fun onOtherCardPlayed(card: Card, player: Player, trick: Trick)
    abstract fun onOtherBetMade(bet: Int, player: Player, round: Round)

    abstract fun onTurnRequested(hand: Hand, trick: Trick): Card
    abstract fun onTurnFailed(hand: Hand, trick: Trick)

    abstract fun onBetRequested(hand: Hand, round: Round): Int?

    abstract fun onOtherTurnRequested(player: Player, trick: Trick)
    abstract fun onOtherBetRequested(player: Player, round: Round)

    abstract fun onOtherTurnReceived(card: Card, player: Player, trick: Trick)

    abstract fun onBetsFinished(bets: Map<Player, Int>, round: Round)

    override fun toString(): String = username

    @Transient lateinit var hand: Hand
    @Transient lateinit var partner: Player
    var teamIndex by Delegates.notNull<Int>()
}