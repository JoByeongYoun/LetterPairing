import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PairingMouseAdapter extends MouseAdapter{
	
	private LetterPairing lp;
	public PairingMouseAdapter(LetterPairing lp){
		this.lp = lp;
	}
	public void mouseEntered(MouseEvent e){
		System.out.println("entered");
	}
	
	public void mousePressed(MouseEvent e){
		System.out.println("pressed");
		//lp.genPair(e.getComponent());
	}
	
	public void mouseReleased(MouseEvent e){
		System.out.println("발동");
		if(! lp.isPairChangeMode()){ //change모드가 아니면
			lp.startChangePairMode(e.getComponent());
			lp.cLp = lp;
		}
		else{// 맞으면 genpair 
			//전에 선택한 버튼이 있는지 확인하고 genpair
			if(lp.getGenPairFlag())
			{
				lp.genPair(e.getComponent());
				lp.cLp = null;
			}
		}
	}
	
	public void mouseExited(MouseEvent e){
		System.out.println("exited");
	}

}
