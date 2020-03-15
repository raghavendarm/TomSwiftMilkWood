# TomSwiftMilkWood - Trigrams
Problem - http://codekata.com/kata/kata14-tom-swift-under-the-milkwood/

## Execution

MultiThread approach: TomSwiftMilkwood.java -> main()

Single Thread approach: SimpleSingleThrdTrigramCipher.java -> main()

## Book resources
/filepath/BigBook.txt - passed as input to the main method

## Output Trigram book
/resources/Trigram*******.txt

## Implementation

the given book is read as a stream and split into chunks of 4096 bytes.
Each TrigramCreator thread is fed with with a chunk of the book

TrigramCreator thread reads the chunk of the book and creates TrigramModel
TrigramModel comprises of 2 maps - mapofTrigrams and chunkWordCounterMap.
mapofTrigrams = 2 consecutive words make key, 3rd word is value
chunkWordCounterMap = chunkNo -> no of words in the chunk.

Each TrigramWriter thread is fed with generatedTrigramModel from the TrigramCreator threads, output file, chunk no.
Each TrigramWriter thread will get a random trigram from the model and writes a chunk to file match no of words on the chunk.
If the trigram cannot be found in the map, a random key is again generated.

After the end of execution a new file is created at the input file location with name Trigram*******.txt.


## Developer Notes
main method takes the input of the book file (.txt)
Example Scanner input - /Users/rm/Desktop/TomSwiftMilkwood/src/main/resources/BigBook.txt



