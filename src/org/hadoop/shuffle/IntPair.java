package org.apache.hadoop.shuffle;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

/**
 * 自定义输出key输出类型
 * @author hadoop
 *
 */
public class IntPair implements WritableComparable<IntPair>{

	 private int first;
	 private int second;
	 
	 
	public IntPair() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public IntPair(int first,int second) {
		super();
		this.first = first;
		this.second = second;
	}
	
	
	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		 this.first = in.readInt();
		 this.second = in.readInt();
		
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(this.first);
		out.writeInt(this.second);
		
	}

	//java.util.Comparator 接口。要实现里面的函数 int compare(Object o1, Object o2) 返回一个基本类型的整型，返回负数表示o1 小于o2，返回0 表示o1和o2相等，返回正数表示o1大于o2。 
	@Override
	public int compareTo(IntPair o) {
		if(o == this){
			return 0;
		}
		//先按照first排序
		int tmp = Integer.compare(this.first, o.first);
		//如果first不等直接返回
		if(tmp != 0){
			return tmp;
		}
		//first不等再按照second排序
		tmp = Integer.compare(this.second, o.second);
		return tmp;
	}

}
