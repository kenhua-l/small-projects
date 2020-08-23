# CS4248 Assignment 3
## Text Classifier
### Written by Liew Ken Hua (A0112230H)

This code is written in Java. Please ensure you have Java installed in your machine to run the code.
If the java class files are not included, it is good to compile the java files first.

Compile porter.java first.
`javac porter.java`

Then the textClassifier codes
`javac tc_train.java`

`javac tc_test.java`


To build the learning perceptron model with the training file, run the following

`java tc_train stopword-list train-class-list model`

After building the model, run the classifier on the test file

`java tc_test stopword-list model test-list test-class-list`

## tc_train

tc_train first set up the parameters before text training.
setStopWordList() is to read all the stop words from the stopword-list
readTrainClassList() will then read the training texts one round to set up some parameters like
the X^2 value and the number of output class. It also computes variables used in selectFeature()
to select the best features to be considered for learning. The X^2 value is computed in
getChiSquareValue().

After the first read, a NeuralNet object is set up. This is a Neural Net with no hidden layers.
Each of the the perceptron unit corresponds to the output classes.
In neuralNetLearning(), the training texts are re-read as much as the number of decided iteration.
Each read of a training text list will call neuralNetworkTraining() which is the primary method
that manipulates the NeuralNet object.

neuralNetworkTraining() will create an inputVector and targetVector for each training text using
getInputVector() and getTargetVector(). These two vectors are then used to call the feedForwardBackLearn()
method from NeuralNet object. feedForwardBackLearn() first does a justFeedForward(), and then
backpropagates the error and update the weights

Finally, after learning, the selected features, output classes, and the updated NeuralNet weights
are recorded in the model file using writeModel().

Note that, NeuralNet is also used in tc_test.

## tc_test

In tc_test, the stop words are first defined using setStopWordList().
Then, it calls readModelFile() to set up the NeuralNet object which will be used for
feed-forward text-classification.
In classifyUsingNet, each training text read is given an input vector using
getInputVector(). The input vector is then fed into the NeuralNet for feed-forward
to get the outputVector. The class of the text is determined from the outputVector
using getClass().
