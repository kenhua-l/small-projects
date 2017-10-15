# CS4248 Assignment 2
## POS Tagger
### Written by Liew Ken Hua (A0112230H)

If the java class file is not included, it is good to compile the java files first.

`javac build_tagger.java`

`javac run_tagger.java`


To build the tagger with the training file, run the following

`java build_tagger sents.train sents.devt model_file`

After building the model, run the tagger on the test file

`java run_tagger sents.test model_file sents.out`
