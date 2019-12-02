package ai;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.util.ArrayUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

public class Model2 {
    private int inputDim;
    private int outputDim;
    private int numOfLabels;
    private ComputationGraphConfiguration config;
    private ComputationGraph net;

    public Model2(int inputDim, int numOfLabels, float learningRate) {
        int rngSeed = 123;
        config = new NeuralNetConfiguration.Builder()
                .seed(rngSeed)
                .updater(new Adam(learningRate))
                .activation(Activation.RELU)
                .l2(0.0001)
                .weightInit(WeightInit.XAVIER)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .convolutionMode(ConvolutionMode.Same)
                .graphBuilder()
                .addInputs("input")
                .layer(0, new ConvolutionLayer.Builder(new int[]{5, 5}, new int[]{1, 1}, new int[]{0, 0})
                        .nIn(inputDim)
                        .nOut(32)
                        .build(), "input")
                .layer(1, new ConvolutionLayer.Builder(new int[]{5, 5}, new int[]{1, 1}, new int[]{0, 0})
                        .nIn(32)
                        .nOut(64)
                        .build(), "0")
                .layer(2, new ConvolutionLayer.Builder(new int[]{5, 5}, new int[]{1, 1}, new int[]{0, 0})
                        .nIn(64)
                        .nOut(64)
                        .build(), "1")
                .layer(3, new SubsamplingLayer.Builder()
                        .poolingType(SubsamplingLayer.PoolingType.MAX).kernelSize(3, 3)
                        .stride(2, 2).build(), "2")
                .layer(4, new DenseLayer.Builder().activation(Activation.RELU)
                        .nOut(224)
                        .build(), "3")
                .layer(5, new OutputLayer.Builder()
                        .nOut(numOfLabels)
                        .activation(Activation.SOFTMAX)
                        .lossFunction(LossFunctions.LossFunction.MSE)
                        .build(), "4")
                .setOutputs("5")
                .setInputTypes(InputType.convolutional(15, 15, 1))
                .build();
        net = new ComputationGraph(config);
        net.init();
    }

    public Model2(int inputDim, int numOfLabels, float learningRate, String filePath) throws IOException {
        net = ComputationGraph.load(new File(filePath), true);
        net.init();
    }

    public float[] forward(INDArray state) {
        INDArray resultNDarray = net.output(false, state)[0].reshape(3);
        float[] result = resultNDarray.data().asFloat();
        return result;
    }

    public void fit(INDArray state, INDArray[] target) {
        INDArray[] data = new INDArray[1];
        data[0] = state;
        net.fit(data, target);
    }

    public void save_model() throws IOException {
        File locationToSave = new File("G:/Project/java/CCLT/src/ai/MyComputationGraph2.zip");
        net.save(locationToSave, true);
    }

    public void backup_model(int count) throws IOException {
        if(count % 10 == 0 && count != 0){
            int numOfBackUp = count / 10;
            String fileName = "G:/Project/java/CCLT/src/ai/MyComputationGraphBackUp" + Integer.toString(numOfBackUp) + ".zip";
            File locationToSave = new File(fileName);
            net.save(locationToSave, true);
        }
    }
}
