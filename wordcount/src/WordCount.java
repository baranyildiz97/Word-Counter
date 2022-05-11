//libraries
import java.io.IOException;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.util.StringTokenizer;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.conf.Configuration;

//Main class
public class WordCount {

	//Reduce txt file codes
    public static class reduce extends Reducer<Text,IntWritable,Text,IntWritable> {
    	//New Reducer
        private IntWritable WReduce = new IntWritable();
        //reduce function
        public reduce(Text myKey, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

            int total = 0;
            for (IntWritable val : values) {
                total += val.get();
            }

            WReduce.set(total);
            context.write(myKey, WReduce);
        }
    }
    //Map txt file codes
    public static class map extends Mapper<Object, Text, Text, IntWritable> {
    	//New Mapper
        private final static IntWritable myMapper = new IntWritable(1);
        //Text file
        private Text WMap = new Text();
        //Mapper function
        public map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer tkn = new StringTokenizer(value.toString());
            while (tkn.hasMoreTokens()) {
                WMap.set(tkn.nextToken());
                context.write(WMap, myMapper);
            }
        }
    }
    //output codes
    public static void main(String[] args) throws Exception {

        Configuration cfg = new Configuration();
        Job job = new Job(cfg, "WordCount");
        job.setJarByClass(WordCount.class);
        job.setMapperClass(map.class);
        job.setCombinerClass(reduce.class);
        job.setReducerClass(reduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}