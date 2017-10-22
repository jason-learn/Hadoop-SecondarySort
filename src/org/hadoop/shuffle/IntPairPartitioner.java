package org.apache.hadoop.shuffle;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Partitioner;


public class IntPairPartitioner extends Partitioner<IntPair, IntWritable> {

	@Override
	public int getPartition(IntPair key, IntWritable value, int numPartitions) {
		if(numPartitions>=2){
			int first = key.getFirst();
			if(first % 2==0){
				//key为偶数需要第二个reducer进行处理
				return 1;
			}
			else{
				//key为奇数需要第一个reducer进行处理
				return 0;
			}
			
		}else{
			throw new IllegalArgumentException("reducer个数大于partition个数");
		}
		 
	}

 

}
