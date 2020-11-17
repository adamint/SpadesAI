package spades.utils

fun main() {
    val data = """Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 10 rounds, with winners [HeuristicPlayer1, HeuristicPlayer2]
Team 1 score: -256
Team 2 score: 271
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 8 rounds, with winners [DecisionTreePlayer1, DecisionTreePlayer2]
Team 1 score: 262
Team 2 score: 150
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 5 rounds, with winners [DecisionTreePlayer1, DecisionTreePlayer2]
Team 1 score: 281
Team 2 score: 103
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 11 rounds, with winners [DecisionTreePlayer1, DecisionTreePlayer2]
Team 1 score: 273
Team 2 score: -38
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 23 rounds, with winners [DecisionTreePlayer1, DecisionTreePlayer2]
Team 1 score: 282
Team 2 score: 140
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 29 rounds, with winners [DecisionTreePlayer1, DecisionTreePlayer2]
Team 1 score: 254
Team 2 score: -240
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 8 rounds, with winners [DecisionTreePlayer1, DecisionTreePlayer2]
Team 1 score: 272
Team 2 score: 160
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 22 rounds, with winners [HeuristicPlayer1, HeuristicPlayer2]
Team 1 score: -30
Team 2 score: 251
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 11 rounds, with winners [HeuristicPlayer1, HeuristicPlayer2]
Team 1 score: 54
Team 2 score: 270
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 64 rounds, with winners [DecisionTreePlayer1, DecisionTreePlayer2]
Team 1 score: 250
Team 2 score: 151
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 10 rounds, with winners [DecisionTreePlayer1, DecisionTreePlayer2]
Team 1 score: 260
Team 2 score: 210
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 34 rounds, with winners [HeuristicPlayer1, HeuristicPlayer2]
Team 1 score: -20
Team 2 score: 284
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 5 rounds, with winners [DecisionTreePlayer1, DecisionTreePlayer2]
Team 1 score: 252
Team 2 score: 181
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 5 rounds, with winners [DecisionTreePlayer1, DecisionTreePlayer2]
Team 1 score: 250
Team 2 score: 105
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 18 rounds, with winners [DecisionTreePlayer1, DecisionTreePlayer2]
Team 1 score: 263
Team 2 score: 122
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 35 rounds, with winners [DecisionTreePlayer1, DecisionTreePlayer2]
Team 1 score: 272
Team 2 score: 152
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 12 rounds, with winners [HeuristicPlayer1, HeuristicPlayer2]
Team 1 score: 0
Team 2 score: 303
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 17 rounds, with winners [HeuristicPlayer1, HeuristicPlayer2]
Team 1 score: -20
Team 2 score: 271
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 13 rounds, with winners [HeuristicPlayer1, HeuristicPlayer2]
Team 1 score: -70
Team 2 score: 262
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 19 rounds, with winners [DecisionTreePlayer1, DecisionTreePlayer2]
Team 1 score: 301
Team 2 score: -88
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 9 rounds, with winners [HeuristicPlayer1, HeuristicPlayer2]
Team 1 score: 0
Team 2 score: 275
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 16 rounds, with winners [HeuristicPlayer1, HeuristicPlayer2]
Team 1 score: -39
Team 2 score: 271
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 13 rounds, with winners [HeuristicPlayer1, HeuristicPlayer2]
Team 1 score: 171
Team 2 score: 330
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 8 rounds, with winners [DecisionTreePlayer1, DecisionTreePlayer2]
Team 1 score: 280
Team 2 score: 131
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) facing Team 2 ([HeuristicPlayer1, HeuristicPlayer2])
Over after 6 rounds, with winners [HeuristicPlayer1, HeuristicPlayer2]
Team 1 score: -40
Team 2 score: 272
Team 1 ([DecisionTreePlayer1, DecisionTreePlayer2]) won 56.0% of the time against Team 2 ([HeuristicPlayer1, HeuristicPlayer2])"""

    data.split("\n")
        .chunked(4)
        .map { it.subList(1, it.size) }
        .filter { it.isNotEmpty() }
        .mapIndexed { i, game ->
            println(game)
            val rounds = "Over after (.+) rounds.+".toRegex().matchEntire(game.first())!!.groups[1]!!.value.toInt()
            val team1Score = game[1].removePrefix("Team 1 score: ").toInt()
            val team2Score = game[2].removePrefix("Team 2 score: ").toInt()

            val scoreString = if (team1Score > team2Score) "$team1Score - $team2Score" else "$team2Score - $team1Score"

            "${i + 1} & ${if (team1Score > team2Score) "DecisionTreePlayers" else "BasicHeuristicPlayers"} " +
                    "& $scoreString & $rounds${if (i != 24) " \\\\" else ""}"
        }.forEach { println(it) }




}