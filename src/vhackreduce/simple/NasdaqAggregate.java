package vhackreduce.simple;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
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
import org.apache.hadoop.util.ToolRunner;
import org.hackreduce.mappers.ModelMapper;
import org.hackreduce.models.StockExchangeDividend;


public class NasdaqAggregate extends Configured implements Tool
{
    public enum Count 
    {
        STOCK_SYMBOLS,
        RECORDS_SKIPPED,
        RECORDS_MAPPED
    }

    /*
     * K, V, K1, V1
     * The key value pair received by the mapper (K, V) depends on the InputFormat implementation used
     * Regular TextInputFormat is LongWritable, Text
     * 
     * K1, V1 is implementation dependent
     * here we are mapping stock symbol to the dividends
     */
    public static class HighestDividendMapper extends Mapper<LongWritable, Text, Text, DoubleWritable>
    {
        private static final Logger LOG = Logger.getLogger(HighestDividendMapper.class.getName());
        
        public void map(LongWritable key, Text value, Context context)
        {
            try 
            {
                StockExchangeDividend record = new StockExchangeDividend(value);
                context.write(new Text(record.getStockSymbol()), new DoubleWritable(record.getDividend()));
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
    
    
    /*
     * K1, V1, K2, V2
     * K1, V1 is the output of map
     * K2, V2 is the result of the reduction
     * Here we are mapping stock symbol to the highest dividend
     */    
    public static class HighestDividendReducer extends Reducer<Text, DoubleWritable, Text, Text> 
    {
        private static NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
        
        @Override
        protected void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException 
        {
            context.getCounter(Count.STOCK_SYMBOLS).increment(1);

            double highestDividend = 0.0;
            double averageDividend = 0.0;
            long count = 0;
            
            for (DoubleWritable value : values) 
            {
                highestDividend = Math.max(highestDividend, value.get());
                averageDividend += value.get();
                ++count;
            }

            averageDividend /= count; 
            
            context.write(key, new Text(currencyFormat.format(highestDividend) + "," + currencyFormat.format(averageDividend)));
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
        job.setMapperClass(HighestDividendMapper.class);
        job.setReducerClass(HighestDividendReducer.class);
        
        // This is what the Mapper will be outputting to the Reducer
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(DoubleWritable.class);

        // This is what the Reducer will be outputting
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setInputFormatClass(TextInputFormat.class);
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
