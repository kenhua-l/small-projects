from nltk import pos_tag, word_tokenize

def main():
    sentence = raw_input("What's your sentence?\n")
    for x in pos_tag(word_tokenize(sentence)):
        print x

main()
