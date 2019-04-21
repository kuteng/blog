package MasteringHadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.util.LineReader;

import java.io.IOException;

public class MasteringHadoopCombineFileInputFormat extends CombineFileInputFormat<LongWritable, Text> {
    // 重写 createRecordReader 方法。
    @Override
    public RecordReader<LongWritable, Text> createRecordReader(InputSplit inputSplit,
        TaskAttemptContext taskAttemptContext) throws IOException
    {
        // CombineFileRecordReader 为 “通用RecordReader”，可以为CombineFileSplit里的每个数据块分发不同的recordReader。
        return new CombineFileRecordReader<LongWritable, Text>((CombineFileSplit) inputSplit,
                taskAttemptContext,  MasteringHadoopCombineFileRecordReader.class);
    }

    public static class MasteringHadoopCombineFileRecordReader extends RecordReader<LongWritable, Text> {
        private LongWritable key;
        private Text value;
        private Path path;
        private FileSystem fileSystem;
        private LineReader lineReader;
        private FSDataInputStream fsDataInputStream;
        private Configuration configuration;

        private int fileIndex;
        private CombineFileSplit combineFileSplit;

        private long start;
        private long end;

        public MasteringHadoopCombineFileRecordReader(CombineFileSplit inputSplit,
                TaskAttemptContext context, Integer index) throws IOException
        {
            this.fileIndex = index;
            this.combineFileSplit = inputSplit;

            // 获取配置信息。
            this.configuration = context.getConfiguration();
            this.path = inputSplit.getPath(index);
            this.fileSystem = this.path.getFileSystem(configuration);
            // 获取一个文件输入流。
            this.fsDataInputStream = fileSystem.open(this.path);
            this.lineReader = new LineReader(this.fsDataInputStream, this.configuration);

            this.start = inputSplit.getOffset(index);
            this.end = this.start + inputSplit.getLength(index);

            this.key = new LongWritable(0);
            this.value = new Text("");
        }

        // 该方法只会在构造RecordReader时调用一次。
        @Override
        public void initialize(InputSplit inputSplit, TaskAttemptContext taskAttemptContext)
                throws IOException, InterruptedException
        {
            // 这里代码已经在构造方法中写过了。
        }

        /**
         * 读取下一个键值对，如果成功返回true。
         */
        @Override
        public boolean nextKeyValue() throws IOException, InterruptedException {
            int offset = 0;
            boolean isKeyValueAvailable = true;

            if(this.start < this.end) {
                offset = this.lineReader.readLine(this.value);
                this.key.set(this.start);
                this.start += offset;
            }

            if(offset == 0) {
                this.key.set(0);
                this.value.set("");
                isKeyValueAvailable = false;
            }

            return isKeyValueAvailable;
        }

        /**
         * 获得当前的 Key 。
         */
        @Override
        public LongWritable getCurrentKey() throws IOException, InterruptedException {
            return key;
        }

        /**
         * 获得当前的 Value 。
         */
        @Override
        public Text getCurrentValue() throws IOException, InterruptedException {
            return value;
        }

        /**
         * 获得当前进度。返回值范围是[0, 1]。
         */
        @Override
        public float getProgress() throws IOException, InterruptedException {
            long splitStart = this.combineFileSplit.getOffset(fileIndex);

            if(this.start < this.end) {
                return Math.min(1.0f, (this.start -  splitStart)/ (float) (this.end - splitStart));
            }

            return 0;
        }

        @Override
        public void close() throws IOException {
            if(lineReader != null){
                lineReader.close();
            }
        }
    }
}
