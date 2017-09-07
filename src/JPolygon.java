import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;

import javax.swing.JComponent;

public class JPolygon extends JComponent{
	private Polygon poly;
	public JPolygon(Polygon poly){
		//setPreferredSize(new Dimension(1000, 1000));
		this.poly = poly;
	}
	
	 public void paintComponent(Graphics g){
		    super.paintComponents(g);
		    g.setColor(Color.BLUE);
		    g.drawPolygon(this.poly);
		    this.setOpaque(true);
		    
	 }
	 @Override
		public Dimension getPreferredSize() {
			return new Dimension(1000, 1000);
		}
}
