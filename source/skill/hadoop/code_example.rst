代码示例
===========================

.. 标注重点代码
.. :emphasize-lines: 3,5

.. code-block:: java

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
      @Override
      public RecordReader<LongWritable, Text> createRecordReader(InputSplit inputSplit,
          TaskAttemptContext taskAttemptContext) throws IOException
      {
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

              this.configuration = context.getConfiguration();
              this.path = inputSplit.getPath(index);
              this.fileSystem = this.path.getFileSystem(configuration);
              this.fsDataInputStream = fileSystem.open(this.path);
              this.lineReader = new LineReader(this.fsDataInputStream, this.configuration);

              this.start = inputSplit.getOffset(index);
              this.end = this.start + inputSplit.getLength(index);

              this.key = new LongWritable(0);
              this.value = new Text("");

          }


          @Override
          public void initialize(InputSplit inputSplit, TaskAttemptContext taskAttemptContext)
                  throws IOException, InterruptedException
          {
              //Overloaded in the constructor.
          }

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

          @Override
          public LongWritable getCurrentKey() throws IOException, InterruptedException {
              return key;
          }

          @Override
          public Text getCurrentValue() throws IOException, InterruptedException {
              return value;
          }

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

.. code-block:: java

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

      public static void main(String args[]) throws IOException, InterruptedException,
              ClassNotFoundException
      {
          GenericOptionsParser parser = new GenericOptionsParser(args);
          Configuration config = parser.getConfiguration();
          String[] remainingArgs = parser.getRemainingArgs();

          Job job = Job.getInstance(config, "MasteringHadoop-CombineDemo");
          job.setOutputKeyClass(LongWritable.class);
          job.setOutputValueClass(Text.class);

          job.setMapperClass(CombineFilesMapper.class);

          job.setNumReduceTasks(0);

          job.setInputFormatClass(MasteringHadoop.MasteringHadoopCombineFileInputFormat.class);
          job.setOutputFormatClass(TextOutputFormat.class);

          FileInputFormat.addInputPath(job, new Path(remainingArgs[0]));
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

.. code-block:: java

  import org.apache.hadoop.conf.Configuration;
  import org.apache.hadoop.conf.Configured;
  import org.apache.hadoop.fs.FileSystem;
  import org.apache.hadoop.fs.Path;
  import org.apache.hadoop.fs.PathFilter;
  import org.apache.hadoop.io.IntWritable;
  import org.apache.hadoop.io.LongWritable;
  import org.apache.hadoop.io.Text;
  import org.apache.hadoop.mapreduce.Job;
  import org.apache.hadoop.mapreduce.Mapper;
  import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
  import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
  import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
  import org.apache.hadoop.util.GenericOptionsParser;
  import java.io.IOException;
  import java.util.regex.Matcher;
  import java.util.regex.Pattern;

  public static class MasteringHadoopPathAndSizeFilter extends Configured implements PathFilter {
      private Configuration configuration;
      private Pattern filePattern;
      private long filterSize;
      private FileSystem fileSystem;

      @Override
      public boolean accept(Path path){
          boolean isFileAcceptable = true;

          try {
              if(fileSystem.isDirectory(path)) {
                  return true;
              }

              if(filePattern != null) {
                  Matcher m = filePattern.matcher(path.toString());
                  isFileAcceptable = m.matches();
              }

              if(filterSize > 0) {
                  long actualFileSize = fileSystem.getFileStatus(path).getLen();

                  if(actualFileSize > this.filterSize) {
                      isFileAcceptable &= true;
                  }
                  else {
                      isFileAcceptable = false;
                  }
              }
          }
          catch(IOException ioException) {
              //Error handling goes here.
          }

          return isFileAcceptable;
      }

      @Override
      public void setConf(Configuration conf){
          //Your setConf override implementation goes here
          this.configuration = conf;

          if(this.configuration != null) {
              String filterRegex = this.configuration.get("filter.name");

              if(filterRegex != null){
                  this.filePattern = Pattern.compile(filterRegex);
              }

              String filterSizeString = this.configuration.get("filter.min.size");

              if(filterSizeString != null){
                  this.filterSize = Long.parseLong(filterSizeString);
              }

              try{
                  this.fileSystem = FileSystem.get(this.configuration);
              }
              catch(IOException ioException){
                  //Error handling
              }
          }
      }
  }

.. code-block:: java

  package MasteringHadoop;

  import org.apache.hadoop.io.IntWritable;
  import org.apache.hadoop.io.Text;
  import org.apache.hadoop.io.WritableComparable;

  import java.io.DataInput;
  import java.io.DataOutput;
  import java.io.IOException;

  public class CompositeJoinKeyWritable implements WritableComparable<CompositeJoinKeyWritable> {
      private Text key = new Text();
      private IntWritable source = new IntWritable();

      public CompositeJoinKeyWritable(){

      }

      public CompositeJoinKeyWritable(String key, int source){

          this.key.set(key);
          this.source.set(source);

      }

      public IntWritable getSource(){
          return this.source;
      }

      public Text getKey(){
          return this.key;
      }

      public void setSource(int source){
          this.source.set(source);
      }

      public void setKey(String key){
          this.key.set(key);

      }

      @Override
      public void write(DataOutput dataOutput) throws IOException {

          this.key.write(dataOutput);
          this.source.write(dataOutput);
      }

      @Override
      public void readFields(DataInput dataInput) throws IOException {

          this.key.readFields(dataInput);
          this.source.readFields(dataInput);

      }


      @Override
      public int compareTo(CompositeJoinKeyWritable o) {

          int result = this.key.compareTo(o.key);

          if(result == 0){
              return this.source.compareTo(o.source);
          }


          return result;
      }

      @Override
      public boolean equals(Object obj){

          if(obj instanceof CompositeJoinKeyWritable){

              CompositeJoinKeyWritable joinKeyWritable = (CompositeJoinKeyWritable)obj;

              return (key.equals(joinKeyWritable.key) && source.equals(joinKeyWritable.source));
          }

          return false;

      }
  }

  public static class CompositeJoinKeyPartitioner extends Partitioner<CompositeJoinKeyWritable, Text>{
      @Override
      public int getPartition(CompositeJoinKeyWritable key, Text value, int i) {
          return (key.getKey().hashCode() % i);
      }
  }

  public static class CompositeJoinKeyComparator extends WritableComparator{
      protected CompositeJoinKeyComparator(){
           super(CompositeJoinKeyWritable.class, true);

      }

      @Override
      public int compare(Object a, Object b) {
          CompositeJoinKeyWritable compositeKey1 = (CompositeJoinKeyWritable) a;
          CompositeJoinKeyWritable compositeKey2 = (CompositeJoinKeyWritable) b;

          return compositeKey1.getKey().compareTo(compositeKey2.getKey());
      }
  }

  public static class MasteringHadoopReduceSideJoinCountryMap extends Mapper<LongWritable, Text, CompositeJoinKeyWritable, Text>{
      private static short COUNTRY_CODE_INDEX = 0;
      private static short COUNTRY_NAME_INDEX = 1;

      private static CompositeJoinKeyWritable joinKeyWritable = new CompositeJoinKeyWritable("", 1);
      private static Text recordValue = new Text("");

      @Override
      protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

          String[] tokens = value.toString().split(",", -1);

          if(tokens != null){
              joinKeyWritable.setKey(tokens[COUNTRY_CODE_INDEX]);
              recordValue.set(tokens[COUNTRY_NAME_INDEX]);
              context.write(joinKeyWritable, recordValue);
          }


      }
  }

  public static class MasteringHadoopReduceSideJoinCityMap extends Mapper<LongWritable, Text, CompositeJoinKeyWritable, Text>{
      private static short COUNTRY_CODE_INDEX = 0;


      private static CompositeJoinKeyWritable joinKeyWritable = new CompositeJoinKeyWritable("", 2);
      private static Text record = new Text("");

      @Override
      protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

          String[] tokens = value.toString().split(",", -1);

          if(tokens != null){
              joinKeyWritable.setKey(tokens[COUNTRY_CODE_INDEX]);
              record.set(value.toString());
              context.write(joinKeyWritable, record);
          }


      }
  }

  public static class MasteringHadoopReduceSideJoinReduce extends Reducer<CompositeJoinKeyWritable, Text, Text, LongWritable>{
      private static LongWritable populationValue = new LongWritable(0);
      private static Text countryValue = new Text("");
      private static short POPULATION_INDEX = 4;

      @Override
      protected void reduce(CompositeJoinKeyWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
          long populationTotal = 0;
          boolean firstRecord = true;
          String country = null;
          for(Text record : values){

              String[] tokens = record.toString().split(",", -1);
              if(firstRecord){
                  firstRecord = false;
                  if(tokens.length > 1)
                      break;
                  else
                    country = tokens[0];
              }
              else{
                  String populationString = tokens[POPULATION_INDEX];

                  if(populationString != null && populationString.isEmpty() == false){
                      populationTotal += Long.parseLong(populationString);
                  }

              }
          }

          if(country != null){
              populationValue.set(populationTotal);
              countryValue.set(country);
              context.write(countryValue, populationValue);

          }
      }
  }

  public static void main(String args[]) throws IOException, InterruptedException, ClassNotFoundException{
      GenericOptionsParser parser = new GenericOptionsParser(args);
      Configuration config = parser.getConfiguration();
      String[] remainingArgs = parser.getRemainingArgs();

      Job job = Job.getInstance(config, "MasteringHadoop-ReduceSideJoin");

      job.setMapOutputKeyClass(CompositeJoinKeyWritable.class);
      job.setMapOutputValueClass(Text.class);
      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(LongWritable.class);


      job.setReducerClass(MasteringHadoopReduceSideJoinReduce.class);
      job.setPartitionerClass(CompositeJoinKeyPartitioner.class);
      job.setGroupingComparatorClass(CompositeJoinKeyComparator.class);
      job.setNumReduceTasks(3);


      MultipleInputs.addInputPath(job, new Path(remainingArgs[0]), TextInputFormat.class, MasteringHadoopReduceSideJoinCountryMap.class);
      MultipleInputs.addInputPath(job, new Path(remainingArgs[1]), TextInputFormat.class, MasteringHadoopReduceSideJoinCityMap.class);

      job.setOutputFormatClass(TextOutputFormat.class);
      TextOutputFormat.setOutputPath(job, new Path(remainingArgs[2]));

      job.waitForCompletion(true);
  }

.. code-block:: java

  package MasteringHadoop;

  import org.apache.hadoop.conf.Configuration;
  import org.apache.hadoop.fs.FSDataInputStream;
  import org.apache.hadoop.fs.FileSystem;
  import org.apache.hadoop.fs.Path;
  import org.apache.hadoop.io.LongWritable;
  import org.apache.hadoop.io.Text;
  import org.apache.hadoop.mapreduce.*;
  import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
  import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
  import org.apache.hadoop.util.GenericOptionsParser;
  import org.apache.hadoop.util.LineReader;

  import java.io.IOException;
  import java.net.URI;
  import java.net.URISyntaxException;
  import java.util.TreeMap;

  public class MasteringHadoopMapSideJoin {
      public static class MasteringHadoopMapSideJoinMap extends Mapper<LongWritable, Text, Text, LongWritable> {
          private static short COUNTRY_CODE_INDEX = 0;
          private static short COUNTRY_NAME_INDEX = 1;
          private static short POPULATION_INDEX = 4;


          private TreeMap<String, String> countryCodesTreeMap = new TreeMap<String, String>();
          private Text countryKey = new Text("");
          private LongWritable populationValue = new LongWritable(0);


          @Override
          protected void setup(Context context) throws IOException, InterruptedException {

              URI[] localFiles = context.getCacheFiles();

              String path = null;
              for(URI uri : localFiles){
                  path = uri.getPath();
                  if(path.trim().equals("countrycodes.txt")){
                       break;
                  }

              }

              if(path != null){
                 getCountryCodes(path, context);
              }

          }

          private void getCountryCodes(String path, Context context) throws IOException{

              Configuration configuration = context.getConfiguration();
              FileSystem fileSystem = FileSystem.get(configuration);
              FSDataInputStream in = fileSystem.open(new Path(path));
              Text line = new Text("");
              LineReader lineReader = new LineReader(in, configuration);

              int offset = 0;
              do{
                  offset = lineReader.readLine(line);

                  if(offset > 0){
                      String[] tokens = line.toString().split(",", -1);
                      countryCodesTreeMap.put(tokens[COUNTRY_CODE_INDEX], tokens[COUNTRY_NAME_INDEX]);
                  }

              }while(offset != 0);
          }

          @Override
          protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

              String cityRecord = value.toString();
              String[] tokens = cityRecord.split(",", -1);

              String country = tokens[COUNTRY_CODE_INDEX];
              String populationString = tokens[POPULATION_INDEX];

              if(country != null && country.isEmpty() == false){

                  if(populationString != null && populationString.isEmpty() == false){

                      long population = Long.parseLong(populationString);
                      String countryName = countryCodesTreeMap.get(country);

                      if(countryName == null) countryName = country;

                      countryKey.set(countryName);
                      populationValue.set(population);
                      context.write(countryKey, populationValue);

                  }

              }
          }
      }

      public static class MasteringHadoopMapSideJoinReduce extends Reducer<Text, LongWritable, Text, LongWritable>{
          private static LongWritable populationValue = new LongWritable(0);
          @Override
          protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {

               long populationTotal = 0;

               for(LongWritable population : values){
                  populationTotal += population.get();
               }
              populationValue.set(populationTotal);
              context.write(key, populationValue);
          }
      }
  }

