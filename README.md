vHackReduce
===========

Montreal 2012 HackReduce

https://github.com/hackreduce/Hackathon

build the project first

BIXI

java -classpath ".:build/libs/HackReduce-0.2.jar:lib/*" org.hackreduce.examples.bixi.RecordCounter datasets/bixi /tmp/bixi_recordcounts

NASDAQ

java -classpath ".:build/libs/HackReduce-1.0.1.jar:lib/*" vhackreduce.simple.Nasdaq datasets/nasdaq/dividends results/nasdaq_aggregate results/nasdaq_best

java -classpath ".:build/libs/HackReduce-0.2.jar:lib/*" org.hackreduce.examples.stockexchange.HighestDividend datasets/nasdaq/dividends /tmp/nasdaq_dividends
java -classpath ".:build/libs/HackReduce-0.2.jar:lib/*" org.hackreduce.examples.stockexchange.MarketCapitalization datasets/nasdaq/daily_prices /tmp/nasdaq_marketcaps
java -classpath ".:build/libs/HackReduce-0.2.jar:lib/*" org.hackreduce.examples.stockexchange.RecordCounter datasets/nasdaq/daily_prices /tmp/nasdaq_recordcounts

NYSE

java -classpath ".:build/libs/HackReduce-0.2.jar:lib/*" org.hackreduce.examples.stockexchange.HighestDividend datasets/nyse/dividends /tmp/nyse_dividends
java -classpath ".:build/libs/HackReduce-0.2.jar:lib/*" org.hackreduce.examples.stockexchange.MarketCapitalization datasets/nyse/daily_prices /tmp/nyse_marketcaps
java -classpath ".:build/libs/HackReduce-0.2.jar:lib/*" org.hackreduce.examples.stockexchange.RecordCounter datasets/nyse/daily_prices /tmp/nyse_recordcounts

Flights

java -classpath ".:build/libs/HackReduce-0.2.jar:lib/*" org.hackreduce.examples.flights.RecordCounter datasets/flights /tmp/flights_recordcounts

Wikipedia

java -classpath ".:build/libs/HackReduce-1.0.1.jar:lib/*" vhackreduce.simple.Wikipedia datasets/wikipedia results/wikipedia_recordcounts

java -classpath ".:build/libs/HackReduce-0.2.jar:lib/*" org.hackreduce.examples.wikipedia.RecordCounter datasets/wikipedia /tmp/wikipedia_recordcounts

Google 1gram

java -classpath ".:build/libs/HackReduce-0.2.jar:lib/*" org.hackreduce.examples.ngram.one_gram.RecordCounter datasets/ngrams/1gram /tmp/1gram_recordcounts

Google 2gram

java -classpath ".:build/libs/HackReduce-0.2.jar:lib/*" org.hackreduce.examples.ngram.two_gram.RecordCounter datasets/ngrams/2gram /tmp/2gram_recordcounts

MSD

java -classpath ".:build/libs/HackReduce-0.2.jar:lib/*" org.hackreduce.examples.msd.RecordCounter datasets/msd /tmp/msd_recordcounts

Streaming example

java -classpath ".:lib/*" org.apache.hadoop.streaming.HadoopStreaming -input datasets/nasdaq/daily_prices/ -output /tmp/py_streaming_count -mapper streaming/nasdaq_counter.py -reducer aggregate


