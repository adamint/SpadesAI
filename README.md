# SpadesAI

The SpadesEngine allows for configuration of teams, points to play to, any number of game 
observers to subscribe to game events, and starting the game. SpadesGame, Round, and Trick emit 
events and run through the game, prompting each player to make bets and play cards. After each round, 
a new round score is added for each team, and end conditions are checked to make sure the game should 
continue.

Please see src/test/kotlin/spades/engine/Tests.kt to understand how to create teams yourselves.

Build and run:
- In IntelliJ IDEA, make sure "Kotlin" plugin is installed and Java is available. Go to src/test/kotlin/spades/engine/Tests.kt 
and click the green Run (play) button next to fun main in the file to run tests. Kotlin 1.4 is required, make sure that 
Kotlin plugin >= 1.4.0 by going to Tools/Kotlin/Configure Kotlin Plugin Updates, JDK required >= 8.