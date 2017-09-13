import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JLabel;

public class Pair { // 한쌍의 Coordinate을 가지는 객체
	private Coordinate left;
	private Coordinate right;
	// pair가 성공한 글자와 안된 글자를 구분하기위해서 어떻게 구조를 수정
	private int pairNum;

	private int leftIdx; // polyCoordiList에서의 idx
	private int rightIdx;

	private JLabel leftLabel;
	private JLabel rightLabel;

	public Pair(int lIdx, int rIdx, ArrayList<Coordinate> coordiList) {
		leftIdx = lIdx;
		rightIdx = rIdx;
		left = coordiList.get(leftIdx);
		right = coordiList.get(rightIdx);
	}

	public Pair(Coordinate l, Coordinate r) {
		left = l;
		right = r;
	}

	public void switchLR(){
		Coordinate tmp; //coordinate switch
		tmp = left;
		left = right;
		right = tmp;

		//화면상에 출력되는 label switch

		setLabel();

	}
	public void setLabel() {
		leftLabel = new JLabel("z" + pairNum + "l");
		rightLabel = new JLabel("z" + pairNum + "r");

		leftLabel.setLocation(left.getX(), left.getY());
		rightLabel.setLocation(right.getX(), right.getY());

		leftLabel.setSize(100, 20);
		rightLabel.setSize(100, 20);
	}

	public JLabel getLeftLabel(){
		return leftLabel;
	}
	public JLabel getRightLabel(){
		return rightLabel;
	}

	public Coordinate getLeft() {
		return left;
	}

	public void setLeft(Coordinate left) {
		this.left = left;
	}

	public Coordinate getRight() {
		return right;
	}

	public void setRight(Coordinate right) {
		this.right = right;
	}

	public void changeDirection() {
		Coordinate temp = left;
		left = right;
		right = temp;
	}

	public void setPairNum(int num) {
		this.pairNum = num;
	}

	public int getPairNum() {
		return this.pairNum;
	}

	private int reverseY(int num) {
		int standard = 500;

		return 2 * standard - num;
	}

	public void printPairList() {
		System.out.println("------pair" + getPairNum() + "------");
		System.out.println("left: " + left.getX() + "," + left.getY());
		System.out.println("right: " + right.getX() + "," + right.getY());
	}

}
