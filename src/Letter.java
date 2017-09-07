import java.util.ArrayList;

public class Letter {
	public int xArray[];
	public int yArray[]; //�ܰ����� ��ǥ��� ���ڸ� ǥ��
	
	public Letter(){
		
	}
	
	public Letter(int xArray[], int yArray[])
	{
		this.xArray = xArray;
		this.yArray = yArray;
	}
	
	public Letter(ArrayList<Integer> xList, ArrayList<Integer> yList){
		xArray = new int[xList.size()];
		yArray = new int[yList.size()];
		
		int i=0;
		
		for(i=0; i<xList.size(); i++){
			xArray[i] = xList.get(i);
			yArray[i] = yList.get(i);
		}
		
	}
	
	public int size(){
		if(xArray.length == yArray.length)
			return xArray.length;
		else 
			return -1;
	}
}
