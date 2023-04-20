# Battle of Monsters

This challenge is about completing a battle of monsters app, where we can create, read, update, and delete battles between different monsters. The app already has some functionalities implemented, but we need to add more to make it complete.

Our goal is to add endpoints to list all monsters, start a battle, and delete a battle. We need to make sure that all the tests are working correctly.

To calculate the battle algorithm, we need to follow some rules. The monster with the highest speed attacks first, and if the speeds are equal, the monster with the higher attack goes first. We need to subtract the defense from the attack to calculate the damage, and if the attack is equal to or lower than the defense, the damage is 1. We need to keep doing this until one monster's HP reaches zero, and that monster is declared the winner.

This project is built using the Java ecosystem libraries, so we need to be familiar with Java, Spring Boot, Gradle, Flyway, Project Lombok, JUnit, and Mockito. Also, we might face some issues in making the app run, and we need to fix them.