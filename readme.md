# Backgammon

An attempt to create one of the oldest known board games.

&nbsp;
## About Backgammon
see [Wikipedia](https://en.wikipedia.org/wiki/Backgammon)
> It is a two player game where each player has fifteen checkers which move between twenty-four points according to the roll of two dice, with the objective to be the first to bear off all fifteen of their checkers off the board.

&nbsp;
## IDE or Text Editors

There are many Java IDE or text editors to choose from, ultimately, which to use is subjective, so choose the one that suits you.

Personally, I prefer Eclipse for Java.

- [Downloading and using Eclipse](https://www.eclipse.org/downloads/)

&nbsp;
## Running the game
1. Open Eclipse, and create a new Java project.
2. Right click the newly created src folder.
3. Click Import..
4. Click General -> File System -> Next.
5. Click Browse..
6. Browse to the folder that contains all the content of the repository.
7. Select that folder.
8. Tick all the files, i.e. constants, events, game_engine, img, etc.
9. Click Finish.
10. Open and run Main.java.

&nbsp;
## Authors
* [bryansng](https://github.com/bryansng)
* [LxEmily](https://github.com/LxEmily)

&nbsp;
## Acknowledgments
- Our parents
- UCD Student Union's coffee machine
- Stack Overflow
- Oracle Documentations

&nbsp;
---
&nbsp;

## Sprint 1 - Short Documentation
### Requirements:
#### UI-wise:
- [x] Board Panel displaying the game.
- [x] Board Panel with number of the pips.
- [x] Board Panel with room for doubling cube and match score.
- [x] Information Panel displaying game information.
- [x] Command Panel allowing users to enter commands.
- [x] Command Panel echo whatever user enters to information panel (NOTE: whitespace not accepted).
- [x] Command Panel terminate application on input quit.

#### Game-wise:
- [x] Board with initial position of players' checkers.
- [x] Board with ability to move checkers around the board, bear-off, bear-on, get hit via mouse or keyboard.

&nbsp;
### How we did it as a whole:
- **_Explore Objected Oriented Design concepts and techniques_**, to learn how to maintain our code. This is why we modularized and objectified almost everything in the application, this can be seen as a bit too much classes, but when it comes down to maintaining, you will know exactly where the problem is (i.e which class contains the culprit).

- **_Separate interactions between objects_**. With OOP, comes the interaction between objects. For two objects to interact with each other, a class is used to facilitate the interaction, this class is called the Controller class.

  The higher up the interaction chain, the more interaction there is, to the point where the top class handles all the interaction between all the objects. The lower down you go, the less interaction there is, to the point where the objects interact with themselves (i.e. initialize, set and get their own instance variables).

  There are many controller classes used for the interaction between objects in this application. i.e. MainController (interaction between GameController, CommandPanel, InfoPanel, RollDieButton), GameController (Bar, Home, Board, UserPanels), Board (Points, Dices, Checkers), Point (Checkers), Bars (Bar), HomePanel (Home), etc.

  If there's a bug in the code, what we do is understand how the objects interact with each other, then go to the class file that handles the interaction, then work our way down the interaction chain to find the bug.

  For instance, for some reason, we typed /move 1 2, a black checker is at point 2, a white checker is at point 1, black checker is supposed to get hit and go to bar. Command is entered, but the black checker doesn't move. We know that /move commands relies on *CommandPanel* to work, relays system messages through *InfoPanel*, and moves are made through *Board*. The code of /move is definitely in MainController, which it is. Then you work your way down from MainController, to GameController or Board, or wherever to fix the bug, relying heavily on Java's error stack trace.