package ai;

public class Experience {
    protected Entry state;
    protected int action;
    protected int[] reward;
    protected Entry nextState;
    protected boolean done;

    public Experience(Entry state, int action, int reward, Entry nextState, boolean done) {
        this.state = state;
        this.action = action;
        this.reward = new int[1];
        this.reward[0] = reward;
        this.nextState = nextState;
        this.done = done;
    }

    public Experience(Experience exp) {
        this.state = exp.state;
        this.action = exp.action;
        this.reward = new int[1];
        this.reward[0] = exp.reward[0];
        this.nextState = exp.nextState;
        this.done = exp.done;
    }
}
