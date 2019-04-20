public class Command {
    // Command holds a user entered command and processes its syntax

    private String input;
    private boolean isValid, isMove, isQuit, isCheat, isDouble;
    private Play play;

    Command() {
        input = "";
        isValid = true;
        isMove = false;
        isCheat = false;
        isDouble = false;
        isQuit = false;
    }

    Command(String input, Plays possiblePlays) {
        this.input = input;
        String text = input.toLowerCase().trim();
        if (text.equals("quit")) {
            isValid = true;
            isMove = false;
            isCheat = false;
            isQuit = true;
        } else if (text.matches("[0-9]+") && Integer.parseInt(text)>0 && Integer.parseInt(text)<=possiblePlays.number()) {
            play = possiblePlays.get(Integer.parseInt(text)-1);
            isValid = true;
            isMove = true;
            isCheat = false;
            isDouble = false;
        } else if (text.equals("cheat") && Backgammon.CHEAT_ALLOWED) {
            isValid = true;
            isMove = false;
            isCheat = true;
            isDouble = false;
        } else if (text.equals("double")) {
            isValid = true;
            isMove = false;
            isCheat = false;
            isDouble = true;
        } else {
            isValid = false;
        }
    }

    public Play getPlay() {
        return play;
    }

    public boolean isValid() {
        return isValid;
    }

    public boolean isMove() {
        return isMove;
    }

    public boolean isQuit() {
        return isQuit;
    }

    public boolean isDouble() {
        return isDouble;
    }

    public boolean isCheat() {
        return isCheat;
    }

    public String toString() {
        return input;
    }
}
