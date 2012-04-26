package vhackreduce.simple;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;


public class Nasdaq
{
    public static void main(String[] args) throws Exception 
    {
        if (args.length != 3) {
            System.err.println("Usage: " + Nasdaq.class.getName() + " <input> <output1> <output2>");
            System.exit(2);
        }
        
        int result1 = ToolRunner.run(new Configuration(), new NasdaqAggregate(), args);
        int result2 = ToolRunner.run(new Configuration(), new NasdaqBest(), args);
        
        System.out.println(result1 + " " + result2);
    }
        
}
