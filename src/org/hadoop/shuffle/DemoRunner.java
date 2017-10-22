package org.apache.hadoop.shuffle;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
 
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 主类
 * @author hadoop
 *
 */
public class DemoRunner {
	/**
	 * 处理mapper类
	 * @author hadoop
	 *
	 */
	static class DemoMapper extends Mapper<Object,Text,IntPair,IntWritable>{

		@Override
		protected void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			String[] strs = line.split("\\s");
			if(strs.length==2){
				int first = Integer.valueOf(strs[0]);
				int second = Integer.valueOf(strs[1]);
				context.write(new IntPair(first,second), new IntWritable(second));
			}else{
				System.out.println("数据异常:" +line);
			}
		}
		
	}
	
	/**
	 * 自定义实现reducer
	 * @author hadoop
	 *
	 */
	static class DemoReducer extends Reducer<IntPair,IntWritable,IntWritable,Text>{
		@Override
		protected void reduce(IntPair key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			Integer preKey = key.getFirst();
			StringBuffer sb = new StringBuffer();
			 for(IntWritable value:values){
				 int curKey = key.getFirst();
				 if(preKey==curKey){
					 //表示是同一个key,但是value是不一样的或者是排序好的
					 sb.append(value.get()).append(",");
				 }else{
					  //表示是一个新的key，先输出旧key的value信息，然后修改key值和stringbuffer值
					 context.write(new IntWritable(preKey),new Text(sb.toString()));
					 preKey = curKey;
					 sb = new StringBuffer();
					 sb.append(value.get()).append(",");
				 }
			 }
			 //输出最后一组信息
			 context.write(new IntWritable(preKey), new Text(sb.toString()));
		}
		
	}
	
	public static void main(String[] args) throws Exception{
		//// 读取hadoop配置
		Configuration conf = new Configuration();
		//设置默认集群
		conf.set("fs.defaultFS", "hdfs://localhost:9000");
		// 实例化一道作业
		Job job = Job.getInstance(conf, "demo-job");
		job.setJarByClass(DemoRunner.class);
		// Mapper类型
		job.setMapperClass(DemoMapper.class);
		// Reducer类型
		job.setReducerClass(DemoReducer.class);
		
		
		// map 输出Key的类型
		job.setMapOutputKeyClass(IntPair.class);
		// map输出Value的类型
		job.setMapOutputValueClass(IntWritable.class);
		// rduce输出Key的类型
		job.setOutputKeyClass(IntWritable.class);
		// rduce输出Value的类型
		job.setOutputValueClass(Text.class);
		
		
		//group分组排序
		job.setGroupingComparatorClass(IntPairGrouping.class);
		//设置partitioner
		job.setPartitionerClass(IntPairPartitioner.class);
		job.setNumReduceTasks(2);
		
		//设置路径
		FileInputFormat.addInputPaths(job, "/user/hadoop/input/data.txt");
		FileOutputFormat.setOutputPath(job, new Path("/user/hadoop/output/"+System.currentTimeMillis()));
		job.waitForCompletion(true);
		
	}
 

}
