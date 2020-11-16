package spades.models

import spades.utils.BetConstants
import spades.utils.sum

data class TeamRoundScore(
    val teamBets: Pair<Int, Int>,
    val teamMade: Pair<Int, Int>,
    @Transient val previousScore: TeamRoundScore?,
    val teamBagsBefore: Int = previousScore?.teamBagsAfter ?: 0,
    val teamBags: Int = if (teamMade.sum() - teamBets.sum() > 0) teamMade.sum() - teamBets.sum() else 0,
    val teamExceedsTenBags: Boolean = teamBagsBefore + teamBags >= 10,
    val teamBagsAfter: Int = if (teamExceedsTenBags) teamBagsBefore + teamBags - 10 else teamBagsBefore + teamBags,
    val pointsMade: Int = {
        var points = 0
        if (teamExceedsTenBags) points -= 100
        if (teamBets.first == BetConstants.Nil) {
            if (teamMade.first != 0) points -= 100
            else points += 100
        }
        if (teamBets.second == BetConstants.Nil) {
            if (teamMade.second != 0) points -= 100
            else points += 100
        }

        if (teamBets.sum() > teamMade.sum()) points -= teamBets.sum() * 10
        else points += teamBets.sum() * 10

        points
    }.invoke(),
    val pointsAfter: Int = { (previousScore?.pointsAfter ?: 0) + pointsMade }.invoke(),
    val representableScore: Int = pointsAfter + teamBags
)