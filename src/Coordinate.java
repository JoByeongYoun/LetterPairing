import javax.swing.JLabel;

public class Coordinate {
	private int x;
	private int y;
	
	private JLabel buttonImg;
	
	public Coordinate(int x, int y){
		this.x = x;
		this.y = y;
	}
	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public void setY(int y){
		this.y = y;
	}
	
	public void moveTo(int x, int y){
		setX(x);
		setY(y);
	}
	
	public void setButtonImg(JLabel label){
		buttonImg = label;
	}
	
	public JLabel getButtonImg(){
		return this.buttonImg;
	}
}
