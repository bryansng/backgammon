# TeaCup the Backgammon Bot

![Alt text](/bot.png "Backgammon Bot")

We implemented the bot using the lecturer provided code base for compatibility reasons. 

&nbsp;
## Authors
* [bryansng](https://github.com/bryansng)
* [LxEmily](https://github.com/LxEmily)
* [Abuden](https://github.com/Abuden)

&nbsp;
## Sprint 5 - Bot Documentation
### Requirements:
|     | Requirements              | Our interpretation        |
| --- | ------------------------- | ------------------------- |
| <ul><li>[x] </li></ul> | Generate list of possible plays.  | 👍 |
| <ul><li>[x] </li></ul> | Generate all possible resulting board positions, and assign a score to each. The score reflects the probability that the bot will win from that position. Bot selects play with the highest score. | Implement evaluation function, features, weights, and doubling by referencing the slides, using Chris' code base. |
| <ul><li>[x] </li></ul> | All classes and API other than the bot class is not changed, and the bot is unable to cheat. | 👍 |

&nbsp;
### Who did what:
see [Trello](https://trello.com/b/A4LqsqAB/backgammon)

| Bryan         | Emily         | Braddy        |
| ------------- | ------------- | ------------- |
| Implemented evaluation function, features, and weights reinforcement learning. | Generated and analyzed weights. Documentation and testing. | Implemented doubling. |

&nbsp;
### Implementation Details:
#### getCommand()
**Returns only two possible commands:**
1. Proposing to double the stakes, i.e. "double"
2. Choice of move to make, i.e. "1" (for the first move).

**Evaluation of "Proposing a double":**

_Steps to decide whether to propose:_
1. Calculate winning chance of bot against opponent. Calculation done by calculateWinningChance(). First we score bot and its opponent's current board position, then normalize the scores to probabilities, then we convert it to percentages.
2. Consider whether to double. Done by considerOfferingDoubleDice(). Design of consideration based on lecture slides.

**Evaluation of "Choice of move to make":**

_Steps to get the optimal move:_
1. Generate a list of all possible plays. This is given as function argument.
2. Generate all possible resulting board positions, i.e. position after each play has been made. getResultingBoardPositions() takes care of this, it takes in the list of all possible plays, and returns an ArrayList of PlaysAndBoardPositions objects. PlaysAndBoardPositions is a class that holds only two instance variables, the play and the resulting board positions of that play. To calculate the resulting board positions, we simply go through all possible plays, apply them on a phantom copy of the current board positions.
3. Go through the board positions and score them. The score should reflect the probability that the bot will win from that position. getBoardPositionsProbabilities() handles this. For each resulting board positions generated from the previous step, it scores them. Once all resulting board positions are scored, getProbabilities() go through the scores and normalizes them into probabilities (0.0 - 1.0).
4. In reality, that is very hard to calculate, so the score should be an estimate of how good the position is. getScore() handles this, it takes in a resulting board position and the play that leads to it. Scores are evaluated depending on the state of bot (i.e. opposedBearOff, UnopposedPreBearOff, UnopposedBearOff and Normal). The scores are calculated from the sum of the features that are considered important during those states.
5. The bot should select the play with the highest score (i.e. the highest probability of the bot winning). getBestMove() executes this, it takes in the list of probabilities calculated in step 3, and returns the index of that probability. The zero-based index returned plus 1 will give us the command of the optimal move.

**Features (there are 13 features in total, 9 from slides, 4 derived by the team):**

| No.  | Features         | Function      | Notes                  | 
| ---- | ---------------- | ------------- | ---------------------- |
| 1 | Pip count difference | pipCountDiff | Difference between the total number of dice results needed to bear-off. |
| 2 | Block blot difference | blockBlotDiff | Number of blocks by bot subtracted by the number of blots by opponent. |
| 3 | Number of home board blocks | numHomeBoardBlocks | Number of pips in home board with more than 1 checker. |
| 4 | Length of prime with captured checker | lengthPrimeCapturedChecker | Number of contiguous pips with more than 1 checkers times number of captured opponent checkers. |
| 5 | Anchors | anchor | Number of checkers in opponent's home board. |
| 6 | Number of escaped checkers | numEscapedCheckers | Number of checkers that are not able to get hit, i.e. no opponent checkers after them. |
| 7 | Number of checkers in home board | numCheckersInHomeBoard | Number of checkers in home board. |
| 8 | Number of checkers taken off | numCheckersTakenOff | Number of checkers that bore off. |
| 9 | Number of pips covered | numPipsCovered | Number of pips where bot has more than 1 checkers. |
| 10 | Blot without contest | blotWithoutContest | If bot were to blot, it blots further from opponent's checkers. Idea was to reduce the chances of getting hit. |
| 11 | Priming defense | primingDefense | Opposite of 4, it calculates the same thing but for the opponent. Inspiration for this feature was to allow the bot to evade opponent's priming. |
| 12 | Blot further from home | blotFurtherFromHome | Doing blots are inevitable, but if the bot were to blot, it better be further from home, else it is better to not blot at all. Idea: Prevents the bot from blotting prematurely at their home boards when there are still opponent checkers in the bot's home board. Considered as a counter to hitCloserToHome(). |
| 13 | Hit closer to home | hitCloserToHome | Hit opponent's checkers if they are closer to their home board. Idea: It requires a lot of dice rolls to get a checker from bar back to its home board. |

&nbsp;
#### getDoubleDecision()
- When it’s the bot's turn, the bot will calculateWinningChance() of bot and opponent, then call considerOfferingDoublingDice() to decide.
- When the bot is offered to double, it calls considerAcceptingDoubleDice() to decide, which also depends on calculateWinningChance().
- When considering to offer or accept a double, calculateGammonRate() and isGammonChangeSignificant() will also be called.
- When calculating gammon rate, isGammon() and getLoser() is called.