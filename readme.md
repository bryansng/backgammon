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

** Please check the correct version of source files.

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

## How we did it as a whole:
- **_Explore Objected Oriented Design concepts and techniques_**, to learn how to maintain our code. This is why we modularized and objectified almost everything in the application, this can be seen as a bit too much classes, but when it comes down to maintaining, you will know exactly where the problem is (i.e which class contains the culprit).

- **_Separate interactions between objects_**. With OOP, comes the interaction between objects. For two objects to interact with each other, a class is used to facilitate the interaction, this class is called the Controller class.

  The higher up the interaction chain, the more interaction there is, to the point where the top class handles all the interaction between all the objects. The lower down you go, the less interaction there is, to the point where the objects interact with themselves (i.e. initialize, set and get their own instance variables).

  There are many controller classes used for the interaction between objects in this application. i.e. MainController (interaction between GameController, CommandPanel, InfoPanel, RollDieButton), GameController (Bar, Home, Board, UserPanels), Board (Points, Dices, Checkers), Point (Checkers), Bars (Bar), HomePanel (Home), etc.

  If there's a bug in the code, what we do is understand how the objects interact with each other, then go to the class file that handles the interaction, then work our way down the interaction chain to find the bug.

  For instance, for some reason, we typed /move 1 2, a black checker is at point 2, a white checker is at point 1, black checker is supposed to get hit and go to bar. Command is entered, but the black checker doesn't move. We know that /move commands relies on *CommandPanel* to work, relays system messages through *InfoPanel*, and moves are made through *Board*. The code of /move is definitely in MainController, which it is. Then you work your way down from MainController, to GameController or Board, or wherever to fix the bug, relying heavily on Java's error stack trace.

&nbsp;
## Sprint 2 - Short Documentation
### Requirements:
|     | Requirements              | Our intepretation         |
| --- | ------------------------- | ------------------------- |
| 01. | Announce the game.        | Welcome players at application start. Game is announced when players start it. |
| 02. | Get player names and tell them their checkers' colours. | A dialog to prompt players to input their names and checker colours when they start the game. |
| 03. | Roll to see who moves first. Roll results are used as starting value. Roll again if dice values are equal. | At game start, roll automatically and prompt players to move based on the rolls. The roll function auto recursively rolls the die if the values are equal, i.e. the player never sees the die results being equal. |
| 04. | Allow players to takes turns to roll and move around the board. | 👍 |
| 05. | Rolls are automatic. | All rolls are automatic at start of game and start of turn. |
| 06. | Moves should be entered as "'starting pip' 'ending pip'", e.g. "6 3". | Moves may be made using "/move # #", "# #", or mouse clicks. |
| 07. | The program returns errors if there are no checkers at the starting pip, if the pip numbers are out of range, or if the command format is invalid. | Errors are returned for the former two. The program echoes whatever is not in the correct command format on the info panel. |
| 08. | Players are allowed to enter a move again after an error. | Players may move so long there are moves to be made (depending on die roll), which is not affected by errors. |
| 09. | Players are only allowed to enter one move at a time. | Move commands only take in 2 arguments, i.e. one source and one destination. |
| 10. | The moves do not have to match the dice roll or avoid opponent blocks yet. The program does not need to check that the move is valid yet. | 👍 |
| 11. | When the player enters "next", the current player's turn is over and the other player should then roll and move. | Player enter "/next" or "next" to switch turns. Die is rolled automatically each turn. |
| 12. | The pip numbers at the edge of the board should be correct for the player who is moving their checkers, ie.e. pip numbers change depending on whose move is it. | Player perspective of pip numbers changes each turn. |
| 13. | Program terminates on "quit". | Done in Sprint 1. Players quit game using "/quit", "quit", or the window close button. |

&nbsp;
### Who did what:
see [Trello](https://trello.com/b/A4LqsqAB/backgammon)

| Bryan         | Emily         |
| ------------- | ------------- |
| Roll to see who moves first. Use roll results as staring values. Roll again if dice values are equal. | Announce the game. (See info panel at application start and game start). |
| Allow players to takes turns to roll and move around the board. | Get player names and tell them their checkers' colours, using a dialog prompt. |
| Error checking on pips, checkers, moves, and commands. Players can move again after an error. Players can only enter one move at a time. | Rolls are automatic (at start of game and start of turn).  |
| Player enters "next" to change turns. | Moves should be entered as "'starting pip' 'ending pip'", e.g. "6 3". (Note that moves may also be made with "/move # #" and mouse clicks.) |
| The pip numbers at the edge of the board should be correct for the player who is moving their checkers. | Documentation. |

Additionally, we also did
- "/reset" command to reset the entire application.
- "/restart" command to restart the game (without losing player info).
- "/help" command to see a list of possible commands
- Restrict what players can do before game starts. (No moving checkers, or changing turns.)
- Parsing players' one-based input as zero-based values in game functions to prevent errors.
- Calculate and validate possible moves depending on die roll.
- Highlight checkers and pips as possible moves.
- Die displayed on board is prettier. (They rotate slightly randomly).
- Refactoring along the way.
- Note that explicitly required commands can be called with or without "/". E.g. /move, /next, /quit.

&nbsp;
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
### Who did what:
see [Trello](https://trello.com/b/A4LqsqAB/backgammon)

| Bryan         | Emily         |
| ------------- | ------------- |
| Board panel, including  checkers, pips, numbers on pips, and space for doubling cube and match score.    | Information panel that displays game info. |
| Initial board displayed with checkers in the correct places     | Command panel that takes in player input.      |
| Move checkers from bar to bear off. (Done with "/test", may also be "/move"d or clicked) | Echo user input from command panel to info panel. Terminate with "quit" (or "/quit") command.      |

Additionally, we also did
- Initialize dice and alternating rolling die between two players
- Additional space for player info
- Home and jail
- Highlighting pips when clicked
- Inform player of result of moving checkers and rolling die
- "Stacking" checkers up so they don't go over the pips
- Ignore player input if it is empty or only contains whitespace
- All our commands start with "/", but simply typing the keyword also works
- Save (or auto save before quitting) contents of info panel to text file
- Prompt player to confirm when quitting game