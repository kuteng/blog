package com.peter.hadoop.wordcount;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 这是一个统计文档每个单词出现次数的MapReduce作业。
 */
public class WordCount {

    public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString());

            // 这里演示了Mapper如何实现一个键值对的输入，输出多次。
            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken());
                // Mapper的输出
                context.write(word, one);
            }
        }
    }

    public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;

            for (IntWritable val : values) {
                sum += val.get();
            }

            result.set(sum);
            context.write(key, result);
        }
    }

    // 驱动类
    public static void main(String[] args) throws Exception {
        // 创建配置对象。
        Configuration conf = new Configuration();
        // 创建任务对象
        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(WordCount.class);
        // 告知Mapper类
        job.setMapperClass(TokenizerMapper.class);
        // 告知Combiner类
        job.setCombinerClass(IntSumReducer.class);
        // 告知Reducer类
        job.setReducerClass(IntSumReducer.class);
        // 告知Key的类型
        job.setOutputKeyClass(Text.class);
        // 告知Value的类型
        job.setOutputValueClass(IntWritable.class);
        // 向InputFormat，告知输入文件的路径。
        FileInputFormat.addInputPath(job, new Path(args[0]));
        // 向InputFormat，告知输出文件的路径。
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        // 启动任务，并等待任务执行结束，最后退出程序。
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}