import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class PairingKeyAdapter extends KeyAdapter {

	ArrayList<LetterPairing> lpList;
	LetterPairing lp;
	
	public PairingKeyAdapter(){// lpList�� �޾ƿͼ� 
		lpList = new ArrayList<LetterPairing>();
	}
	public void addLp(LetterPairing lp){
		lpList.add(lp);
	}
	public void clear(){
		lpList.clear();
	}
		
	public void keyPressed(KeyEvent e){
		System.out.println(e.getKeyText(e.getKeyCode()));
		if(lpList == null)
			return;
		
		//change ��忡 ������ lp�� �ֳ� �˻��Ѵ�.
		for(int i=0; i< lpList.size(); i++){
			lp = lpList.get(i);
			if(lp.isPairChangeMode())
				break;
			//������ ���µ��� change����� lp�� ���ٸ� 
			if(i == lpList.size() - 1)
				return ;
		}
		
		//change ����� lp�� �� ��ü�� lp������ �����
		
		String s = e.getKeyText(e.getKeyCode());
		System.out.print(s);
		
		if(s.equals("g") || s.equals("G")){
			System.out.println("genpair");
			//lp.setPairMode(GENPAIR);
			lp.genPair();
		}
		
		else if(s.equals("s") || s.equals("S")){
			lp.switchLR();
		}
		else if(s.equals("R")){
			lp.removePair();
			
		}
	}
}
