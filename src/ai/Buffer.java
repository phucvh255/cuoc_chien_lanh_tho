package ai;

import java.util.Random;
import java.util.Vector;

public class Buffer {
    private int maxSize;
    private Experience[] buffer;
    private static int numOfBuffer;

    public Buffer(int maxSize){
        this.maxSize = maxSize;
        this.buffer = new Experience[maxSize];
        numOfBuffer = 0;
    }

    public void push(int[][] state, int[] action, float[] reward, int[][][] nextState, boolean[] done) {
        Experience experience = new Experience(state, action, reward, nextState, done);
        buffer[numOfBuffer] = experience;
        numOfBuffer++;
    }

    public void push(Experience exp) {
        Experience experience = new Experience(exp);
        buffer[numOfBuffer] = experience;
        numOfBuffer++;
    }

    public static int getNumOfBuffer() {
        return numOfBuffer;
    }

    Experience[] sample(int batch_size) {
        Random rd = new Random();
        Vector<Integer> v = new Vector<Integer>();
        int iNew = 0;
        Experience[] samples = new Experience[batch_size];
        for (int i = 0; i < batch_size; ) {
            iNew = rd.nextInt(numOfBuffer);
            if (!v.contains(iNew)){
                v.add(iNew);
                samples[i] = new Experience(buffer[iNew]);
                i++;
            }
        }
        return samples;
    }
}
