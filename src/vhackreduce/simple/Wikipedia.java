package vhackreduce.simple;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;


public class Wikipedia
{
    public static void main(String[] args) throws Exception 
    {
        if (args.length != 2) {
            System.err.println("Usage: " + Wikipedia.class.getName() + " <input> <output1>");
            System.exit(2);
        }
        
        int result1 = ToolRunner.run(new Configuration(), new WikipediaRecordCounter(), args);
        
        System.out.println(result1);
    }
        
}
