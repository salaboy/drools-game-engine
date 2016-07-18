Simple Drools Game Engine for creating Escape The Room (point and click) style games.

CI here: https://travis-ci.org/Salaboy/drools-game-engine/builds

#Scenario
You wake up in a room, you don't know where you are, you need to: ESCAPE THE ROOM! 

![alt tag](https://raw.githubusercontent.com/Salaboy/drools-game-engine/master/drools-game-engine-rules/escape-the-maryland-1.jpg)

![alt tag](https://raw.githubusercontent.com/Salaboy/drools-game-engine/master/drools-game-engine-rules/escape-the-maryland-2.jpg)


This example was created originally for the conference JBCNConf (June - 2016)
# Requirements:
- JDK 8
- Maven 3.2.3 + 
- GIT 
- IDE with Maven plugin, and you need to know how to use it ;)

# Instructions:

- Fork this repository (with the fork button, you need to be signed in github.com): http://github.com/salaboy/drools-game-engine/
- clone your forked repository: git clone http://github.com/<your user>/drools-game-engine/
- cd drools-game-engine/
- mvn clean install -DskipTests (if you run it with tests it will fail on purpose, the rules are missing, you need to write them)

# Update your fork from this "blessed" repository
- git stash
- git remote add blessed http://github.com/salaboy/drools-game-engine/
- git fetch blessed master
- git rebase blessed/master
- git stash apply

# Objective
Write the missing rules to make all the test green again! 
There are three incomplete DRL files with their corresponding tests:
- Easy: https://github.com/Salaboy/drools-game-engine/blob/master/drools-game-engine-rules/src/main/resources/rules/conf/house-conformance-rules.drl -> With Test: A_StatelessConformanceTest: https://github.com/Salaboy/drools-game-engine/blob/master/drools-game-engine-rules/src/test/java/org/drools/workshop/rules/tests/A_StatelessConformanceTest.java
- Medium: https://github.com/Salaboy/drools-game-engine/blob/master/drools-game-engine-rules/src/main/resources/rules/game/game-rules.drl With two tests -> https://github.com/Salaboy/drools-game-engine/blob/master/drools-game-engine-rules/src/test/java/org/drools/workshop/rules/tests/B_GameRulesTest.java and https://github.com/Salaboy/drools-game-engine/blob/master/drools-game-engine-rules/src/test/java/org/drools/workshop/rules/tests/C_GameAPITest.java
- Medium/Advanced:  https://github.com/Salaboy/drools-game-engine/blob/master/drools-game-engine-rules/src/main/resources/rules/suggestions/game-suggestions-rules.drl with the test -> https://github.com/Salaboy/drools-game-engine/blob/master/drools-game-engine-rules/src/test/java/org/drools/workshop/rules/tests/D_GameSuggestionRulesTest.java

# Homework
Write more rules, improve the existing ones, create more complex scenarios. 
Some ideas:
- Create sub goals and expose the compleated subgoals via a query
- Generalize the concept of a House to be a building with multiple rooms in multiple floors
- Add the time variable, for example, create a hint mechanism that kicks in after a period of inactivity
- Expose the GameSession interface using Rest Services and wildfly swarm
- Provide your own implementation of GameSession, without using the rule engine, so you can compare both impls
- Add Enemies and rules to avoid entering rooms with enemies

Finally send your Pull Request! or contact me via twitter if you have questions @salaboy

