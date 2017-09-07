import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class PairingKeyAdapter extends KeyAdapter {

	ArrayList<LetterPairing> lpList;
	LetterPairing lp;
	
	public PairingKeyAdapter(){// lpList를 받아와서 
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
		
		//change 모드에 돌입한 lp가 있나 검사한다.
		for(int i=0; i< lpList.size(); i++){
			lp = lpList.get(i);
			if(lp.isPairChangeMode())
				break;
			//끝까지 갔는데도 change모드인 lp가 없다면 
			if(i == lpList.size() - 1)
				return ;
		}
		
		//change 모드인 lp가 이 객체의 lp변수로 저장됨
		
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
