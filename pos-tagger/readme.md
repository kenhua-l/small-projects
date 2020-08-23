# CS4248 Assignment 2
## POS Tagger
### Written by Liew Ken Hua (A0112230H)

This code is written in Java. Please ensure you have Java installed in your machine to run the code.
If the java class files are not included, it is good to compile the java files first.

`javac build_tagger.java`

`javac run_tagger.java`


To build the tagger with the training file, run the following

`java build_tagger sents.train sents.devt model_file`

After building the model, run the tagger on the test file

`java run_tagger sents.test model_file sents.out`

## build_tagger

build_tagger is a trainer that builds the model file that is used for assigning the tags in run_tagger.

For a quick rundown:
1. For each sentences in the training set, build_tagger will call processLine(). In this method, it will build two matrices - tagMatrix and vocabularyMatrix - by providing the raw counts of "tag1 tag2" occurrences and raw counts of "words given tag" occurrences into the matrices.

2. Then, it will smoothen both the counts and assign probability values into the matrices by calling smoothenVocabularyMatrix() and smoothProbabilityTagMatrix(). The smoothing method used is Witten-Bell algorithm.

3. Lastly, the model_file is built by calling the printModelFile() method.

## run_tagger

run_tagger is the tag predictor that tags tokens using the data from model file.

For a quick rundown:
1. Firstly, it has to process the data from model_file. By calling the readModel() method, it build two matrices, the tagMatrix and the vocabularyMatrix with the data in model_file.

2. Using the data, it thens evaluate the test data by calling evaluateTestFile(). In this method, it will process sentences by calling the viterbi() method for each sentence.

3. In viterbi(), the viterbiMatrix is filled up with the corresponding viterbi values calculated using the data from tagMatrix and vocabularyMatrix. It creates a viterbiBack matrix to keep track of all the computed probable tags for each word in the sentence. When the viterbiMatrix is filled, a backtrace stack is used to push the most probable tags starting from the last word's tag, up to the first word's tag.

4. Once the viterbi() method is executed, evaluateTestFile() will continue by writing the output file. Each line written will consist of the sentences with the corresponding tags computed for each token.  
