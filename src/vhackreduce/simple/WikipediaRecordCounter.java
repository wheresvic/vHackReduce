package vhackreduce.simple;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.hackreduce.examples.wikipedia.RecordCounter.Count;
import org.hackreduce.mappers.XMLInputFormat;
import org.hackreduce.mappers.XMLRecordReader;
import org.hackreduce.models.StockExchangeDividend;
import org.hackreduce.models.WikipediaRecord;


public class WikipediaRecordCounter extends Configured implements Tool
{
    public enum Count 
    {
        TOTAL_RECORDS,
        UNIQUE_KEYS,
        RECORDS_SKIPPED,
        RECORDS_MAPPED
    }

    public static class WikipediaRecordCountMapper extends Mapper<Text, Text, Text, LongWritable>
    {
        private static final Logger LOG = Logger.getLogger(WikipediaRecordCountMapper.class.getName());
        
        // Our own made up key to send all counts to a single Reducer, so we can aggregate a total value.
        public static final Text TOTAL_COUNT = new Text("total");

        // Just to save on object instantiation
        public static final LongWritable ONE_COUNT = new LongWritable(1);
        
        public void map(Text key, Text value, Context context)
        {
            try 
            {
                @SuppressWarnings("unused")
                WikipediaRecord record = new WikipediaRecord(value);
                context.getCounter(Count.TOTAL_RECORDS).increment(1);
                context.write(TOTAL_COUNT, ONE_COUNT);
            } 
            catch (Exception e) 
            {
                LOG.log(Level.WARNING, e.getMessage(), e);
                context.getCounter(Count.RECORDS_SKIPPED).increment(1);
                return;
            }
            
            context.getCounter(Count.RECORDS_MAPPED).increment(1);
        }
    }
    
    
    public static class WikipediaRecordCountReducer extends Reducer<Text, LongWritable, Text, LongWritable> 
    {
        // private static NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
        
        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException 
        {
            context.getCounter(Count.UNIQUE_KEYS).increment(1);

            long count = 0;
            for (LongWritable value : values) {
                count += value.get();
            }

            context.write(key, new LongWritable(count));
        }
    }
    
    
    @Override
    public int run(String[] args) throws Exception 
    {
        Configuration conf = getConf();
        
        // Creating the MapReduce job (configuration) object
        Job job = new Job(conf);
        job.setJarByClass(getClass());
        job.setJobName(getClass().getName());

        // Tell the job which Mapper and Reducer to use (classes defined above)
        job.setMapperClass(WikipediaRecordCountMapper.class);
        job.setReducerClass(WikipediaRecordCountReducer.class);
        
        // This is what the Mapper will be outputting to the Reducer
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        // This is what the Reducer will be outputting
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        job.setInputFormatClass(XMLInputFormat.class);
        XMLRecordReader.setRecordTags(job, "<page>", "</page>");
        
        job.setOutputFormatClass(TextOutputFormat.class);
        
        // Setting the input folder of the job 
        FileInputFormat.addInputPath(job, new Path(args[0]));

        // Preparing the output folder by first deleting it if it exists
        Path output = new Path(args[1]);
        FileSystem.get(conf).delete(output, true);
        FileOutputFormat.setOutputPath(job, output);

        return job.waitForCompletion(true) ? 0 : 1;
    }
        
}
