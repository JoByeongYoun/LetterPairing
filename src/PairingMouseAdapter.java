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
		System.out.println("�ߵ�");
		if(! lp.isPairChangeMode()){ //change��尡 �ƴϸ�
			lp.startChangePairMode(e.getComponent());
			lp.cLp = lp;
		}
		else{// ������ genpair 
			//���� ������ ��ư�� �ִ��� Ȯ���ϰ� genpair
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
