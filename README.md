Simple Drools Game Engine for creating Escape The Room (point and click) style games.
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

# Objective
Write the missing rules to make all the test green again! 
There are three incomplete DRL files with their corresponding tests:
- Easy: https://github.com/Salaboy/drools-game-engine/blob/master/drools-game-engine-rules/src/main/resources/rules/conf/house-conformance-rules.drl -> With Test: A_StatelessConformanceTest: https://github.com/Salaboy/drools-game-engine/blob/master/drools-game-engine-rules/src/test/java/org/drools/workshop/rules/tests/A_StatelessConformanceTest.java
- Medium: https://github.com/Salaboy/drools-game-engine/blob/master/drools-game-engine-rules/src/main/resources/rules/game/game-rules.drl With two tests -> https://github.com/Salaboy/drools-game-engine/blob/master/drools-game-engine-rules/src/test/java/org/drools/workshop/rules/tests/B_GameRulesTest.java and https://github.com/Salaboy/drools-game-engine/blob/master/drools-game-engine-rules/src/test/java/org/drools/workshop/rules/tests/C_GameAPITest.java
- Medium/Advanced:  https://github.com/Salaboy/drools-game-engine/blob/master/drools-game-engine-rules/src/main/resources/rules/suggestions/game-suggestions-rules.drl with the test -> https://github.com/Salaboy/drools-game-engine/blob/master/drools-game-engine-rules/src/test/java/org/drools/workshop/rules/tests/D_GameSuggestionRulesTest.java


