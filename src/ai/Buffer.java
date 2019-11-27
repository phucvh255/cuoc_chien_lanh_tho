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

    public void push(Entry state, int action, int reward, Entry nextState, boolean done) {
        Experience experience = new Experience(state, action, reward, nextState, done);
        buffer[numOfBuffer] = experience;
        numOfBuffer++;
    }

    private Experience[] sample(int batch_size) {
        Random rd = new Random();
        Vector<Integer> v = new Vector<Integer>();
        int iNew = 0;
        Experience[] samples = new Experience[batch_size];
        for (int i = 0; i < batch_size; ) {
            iNew = rd.nextInt(numOfBuffer);
            if (!v.contains(iNew)){
                i++;
                v.add(iNew);
                samples[i] = new Experience(buffer[iNew]);
            }
        }
        return samples;
    }
}
