package MasteringHadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;

public class CombineFilesMasteringHadoop {
    public static class CombineFilesMapper extends  Mapper<LongWritable, Text, LongWritable, Text> {
        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException
        {
            context.write(key, value);
        }
    }

    /**
     * 整个MapReduce 的驱动程序。
     */
    public static void main(String args[]) throws IOException, InterruptedException,
            ClassNotFoundException
    {
        GenericOptionsParser parser = new GenericOptionsParser(args);
        Configuration config = parser.getConfiguration();
        String[] remainingArgs = parser.getRemainingArgs();

        Job job = Job.getInstance(config, "MasteringHadoop-CombineDemo");
        job.setOutputKeyClass(LongWritable.class);
        job.setOutputValueClass(Text.class);

        // 设置Mapper类。
        job.setMapperClass(CombineFilesMapper.class);

        // 设置Reduce任务的数量。
        job.setNumReduceTasks(0);

        // 设置InputFormat类。
        job.setInputFormatClass(MasteringHadoop.MasteringHadoopCombineFileInputFormat.class);
        // 设置输出格式类。
        job.setOutputFormatClass(TextOutputFormat.class);

        // 设置输入文件的路径
        FileInputFormat.addInputPath(job, new Path(remainingArgs[0]));
        // 设置输出文件的路径
        TextOutputFormat.setOutputPath(job, new Path(remainingArgs[1]));

        job.waitForCompletion(true);
    }

    /**
     * 计数器Mapper用到的枚举类型。
     */
    public static enum WORDS_IN_LINE_COUNTER{
        ZERO_WORDS,
        LESS_THAN_FIVE_WORDS,
        MORE_THAN_FIVE_WORDS
    }

    /**
     * 一个计数器Mapper。但是每次Hadoop中，没有用到这个Mapper。
     */
    public static class MasteringHadoopCountersMap extends Mapper<LongWritable,
            Text, LongWritable, IntWritable>
    {
        private IntWritable countOfWords = new IntWritable(0); 

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException,
                InterruptedException
        {
            StringTokenizer tokenizer = new StringTokenizer(value.toString());
            int words = tokenizer.countTokens();

            if(words == 0)
                context.getCounter(WORDS_IN_LINE_COUNTER.ZERO_WORDS).increment(1);

            if(words > 0 && words <= 5)
                context.getCounter(WORDS_IN_LINE_COUNTER.LESS_THAN_FIVE_WORDS).increment(1);
            else
                context.getCounter(WORDS_IN_LINE_COUNTER.MORE_THAN_FIVE_WORDS).increment(1);

            countOfWords.set(words);
            context.write(key, countOfWords);
        }
    }
}
