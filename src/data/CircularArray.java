package data;

public class CircularArray {

	private int index = -1;
	private boolean full = false;
	private final int[] array;
	private final int size;
	
	public CircularArray(int size) {
		this.size = size;
		array = new int[size];
	}
	
	public void add(int value) {
		index++;
		if(index >= size) {
			full = true;
			index = 0;
		}
		array[index] = value;
	}
	
	public int average() {
		int sum = 0;
		int end = full ? size : index+1;
		for(int i = 0 ; i < end ; i++) {
			sum += array[i];
		}
		return sum / end;
	}

	public void clear() {
		full = false;
		index = -1;
	}
}
