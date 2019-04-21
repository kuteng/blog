代码示例
===========================

.. 标注重点代码
.. :emphasize-lines: 3,5

使用 CombineFileInputFormat 解决小文件问题
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``CombineFileInputFormat`` 是一个抽象类,它能够帮助我们合并文件,从而指定输入。我们需要重写 ``createRecordReader()`` 方法，这个方法实例化了一个自定义的 ``RecordReader`` 类的对象来读取记录。

``CombineFileRecordReader`` 是通用 ``RecordReader`` ，可以为 ``CombineFileSplit`` 里的每个数据块分发不同的recordReader。

``CombineFileInputFormat`` 类在 ``getSplits()`` 方法中返回一个 ``CombineFileSplit`` 分片对象。每个分片可能合并了来自不同文件的不同块。如果使用 ``setMaxSplitSize()`` 方法设置了分片的最大容量，本地节点的文件将会合并到一个分片中，本地剩余的块将与来自同一机架的其他主机的块合并。然而，如果没有设置这个最大容量，合并操作 **不会在本地主机层面进行** ，它只会在 **同一机架** 内进行合并。如果将 ``setMaxSplitSize()`` 设置为HDFS的块容量，那是默认行为，也就是每个块对应一个分片。

``CombineFileFormat`` 类有一个 ``isSplitable()`` 方法,它的默认返回值为true。如果你确认整个文件需要以一个Map任务来处理,则需要将它设置为返回false。

``MasteringHadoopCombineFileRecordReader`` 是我们自定义的 ``RecordReader`` 类，关注的方法有： **构造方法** 、 ``initialize()`` 、 ``nextKeyValue()`` 方法 、 ``getProgress()`` 和 ``close()`` 。其中， ``nextKeyValue()`` 方法会读取下一个键值对，但不会将 *键值对* 向外界输出，外界需要调用方法 ``getCurrentKey()`` 、 ``getCurrentValue()`` 分别获取 `Key` 与 `Value` 。

``MasteringHadoopCombineFileRecordReader`` 实现中，我们可以发现它使用了 ``FSDataInputStream`` 对文件数据进行读取。 ``InputSplit`` 存放的只是 **文件信息** 的集合，没有内容。

下面的代码为 ``InputFormat`` 的实现：

.. literalinclude:: /_codes/hadoop/code001-MasteringHadoopCombineFileInputFormat.java
  :language: java
  :emphasize-lines: 20, 23-25, 34, 54-55, 76, 126

下面的代码为 ``Mapper`` 类与驱动程序的实现。 ``main`` 方法里的内容就是驱动程序，其中最重要的一行就是设置 ``InputFormat`` 作为 ``job.setInputFormatClass(MasteringHadoopCombine FileInputFormat.class)``  。当程序执行时,得到的标准输出也在代码段的后面列出。

.. literalinclude:: /_codes/hadoop/code001-CombineFilesMasteringHadoop.java
  :language: java

运行的配置是：分片的数量是1,数据集的大小为5 MB,而HDFS块容量为128 MB。
运行的结果是： ::

  14/04/10 16:32:05 INFO input.FileInputFormat: Total input paths to process : 441
  14/04/10 16:32:06 INFO mapreduce.JobSubmitter: number of splits:1

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

