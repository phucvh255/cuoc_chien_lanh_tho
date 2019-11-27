package ai;

public class Agent {
    private float learningRate;
    private float gamma;
    private Buffer replayBuffer;

    public Agent(float learningRate, float gamma, int bufferSize) {
        this.gamma = gamma;
        this.learningRate = learningRate;
        this.replayBuffer = new Buffer(bufferSize);
    }

    public
}
