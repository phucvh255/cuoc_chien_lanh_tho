package ai;

import map.Map;

public class Experience {
    int[][] state;
    int[] actions;
    double[] rewards;
    int[][][] nextStates;
    boolean[] done;
    int numOfNextState;


    public Experience(int[][] state, int[] actions, double[] rewards, int[][][] nextStates, boolean[] done) {
        this.state = state;
        for (int action : actions)
            if (action != -1)
                numOfNextState++;
        this.actions = new int[numOfNextState];
        this.rewards = new double[]{-10000, -10000, -10000};
        this.nextStates = new int[numOfNextState][15][15];
        this.done = new boolean[numOfNextState];
        for(int i = 0; i < numOfNextState; i++) {
            this.actions[i] = actions[i];
            this.rewards[i] = rewards[i];
            this.nextStates[i] = nextStates[i];
            this.done[i] = done[i];
        }
    }

    public Experience(Experience exp) {
        this.state = exp.state;
        this.numOfNextState = exp.numOfNextState;
        this.actions = exp.actions;
        this.rewards = exp.rewards;
        this.nextStates = exp.nextStates;
        this.done = exp.done;
    }
}
