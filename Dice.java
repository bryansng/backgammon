class Dice {
    // Dice holds the details for two dice

    private static final int NUM_DICE = 2;

    private int[] numbers;

    Dice() {
        numbers = new int[]{1, 1};
    }

    Dice(int firstValue, int secondValue) {
        numbers = new int[]{firstValue, secondValue};
    }

    public void rollDice() {
        for (int i=0; i<NUM_DICE; i++) {
            numbers[i] = 1 + (int) (Math.random() * 6);
        }
    }

    public int getDie(int index) {
        return numbers[index];
    }

    public boolean isDouble() {
        return numbers[0] == numbers[1];
    }

    public String getDieAsString(int index) {
        return "[" + numbers[index] + "]";
    }

   public String toString() {
      return "[" + numbers[0] + "," + numbers[1] + "]";
   }

}
