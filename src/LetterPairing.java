import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;

public class LetterPairing {
	static int pairCount = 0;
	static LetterPairing cLp;
	public JFrame frame ; // 창
	public JPanel p ;
	public Polygon poly; //
	private int matrix[][]; // 뼈대를 따기 위해 외각선으로부터 거리를 계산
	private ArrayList<Coordinate> skeleton; // 뼈대 좌표 list
	private ArrayList<Coordinate> polyCoordiList; // 외각선 좌표 list
	ArrayList<Integer> garbageCoordiList;
	private ArrayList<Pair> pairList;
	private ArrayList<Coordinate> notPairList;
	private ArrayList<JLabel> polyButtonList;

	private boolean changePairFlag;
	private boolean genPairFlag; // 수동으로 페어를 생성하는 과정일때 true
	private Component[] genPairArr;
	private Component changePairButton;

	private int si;

	LetterPairing(Letter letter, JFrame frame, JPanel p) { // 입출력으로 얻은 letter를 가져온다.

		// 변수들 초기화
		skeleton = new ArrayList<Coordinate>();
		polyCoordiList = new ArrayList<Coordinate>();
		pairList = new ArrayList<Pair>();
		notPairList = new ArrayList<Coordinate>();
		changePairFlag = false;
		genPairFlag = false;
		genPairArr = new JLabel[2];

		this.frame = frame;
		this.p = p;
		//


		for (int i = 0; i < letter.size(); i++) { // 외각선 좌표 초기화
			polyCoordiList.add(new Coordinate(letter.xArray[i], letter.yArray[i]));
		}

		poly = new Polygon(letter.xArray, letter.yArray, letter.size());
		matrix = new int[1000][1000]; //

		for (int i = 0; i < 1000; i++) {
			for (int j = 0; j < 1000; j++) {
				if (poly.inside(i, j))
					matrix[i][j] = 1;
				else
					matrix[i][j] = 0;
			}
		}


		skeletonizing(matrix); // 거리계산
		setSkeleton();

		p.repaint();
		pairing();
		skeletonSort();

		for (int i = 0; i < pairList.size(); i++) { // 페어링된 버튼 이미지를 표시
			pairList.get(i).getLeft().setButtonImg(printButtonImg(pairList.get(i).getLeft(), "blue"));
			pairList.get(i).getRight().setButtonImg(printButtonImg(pairList.get(i).getRight(), "blue"));
			pairList.get(i).getLeft().getButtonImg().addMouseListener(new PairingMouseAdapter(this));
			pairList.get(i).getRight().getButtonImg().addMouseListener(new PairingMouseAdapter(this));
		}

		for (int i = 0; i < notPairList.size(); i++) { // 페어링 되지 않은 버튼 이미지를 표시
			notPairList.get(i).setButtonImg(printButtonImg(notPairList.get(i), "red"));
			notPairList.get(i).getButtonImg().addMouseListener(new PairingMouseAdapter(this));
		}

		// for(int i=0; i<notPairList.size(); i++ ){ // 페어링 되지 않은 버튼 이미지를 삭제
		//
		// p.remove(notPairList.get(i).getButtonImg());
		// }

	}

	ArrayList<Pair> getPairList(){
		return this.pairList;
	}

	public void setSkeleton() { // 뼈대 만듦
		int max = 0;
		for (int i = 0; i < 1000; i++) { // 내부 점으로 찍음
			if (i == 0 || i == 999) // 모서리 스킵
				continue;
			for (int j = 0; j < 1000; j++) {
				if (j == 0 || j == 999 || matrix[i][j] == 0) // 모서리 스킵, 0스킵
					continue;

				max = 0; // 주변에서 가장 큰 값
				for (int k = i - 1; k <= i + 1; k++) { // 주변에서 가장 큰 값 찾음

					for (int l = j - 1; l <= j + 1; l++) { // max값을 찾음
						if (k == i && j == l)
							continue;
						if (matrix[k][l] > max)
							max = matrix[k][l];
					}
				}

				if (matrix[i][j] >= max) // 뼈대가 맞으면 좌표를 넣는다.
					skeleton.add(new Coordinate(i, j));

			}
		}
	}

	void skeletonTest(int percent) {
		JFrame f; // 창
		f = new JFrame("LetterPairing");
		f.setSize(1000, 1000);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel p;
		int lastSkeletonCoordi = Math.round(skeleton.size() * (percent / 100));
		for (si = 0; si < lastSkeletonCoordi; si++) {
			p = new JPanel() {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.setColor(Color.RED);

					Coordinate point;
					for (int i = 0; i < si/* Math.round(skeleton.size()/2) */; i++) {
						point = skeleton.get(i);
						g.drawLine(point.getX(), point.getY(), point.getX(), point.getY());
					}
				}

				@Override
				public Dimension getPreferredSize() {
					return new Dimension(1000, 1000);
				}

			};

			frame.setContentPane(p);
			frame.pack();
			frame.setVisible(true);
			try {
				Thread.sleep(1);
			} catch (Exception e) {
				e.printStackTrace();

			}

		}
	}

	void skeletonSort() { // 뼈대를 sorting 하는 함수
		double min = 9999999; // 뼈대 좌표한에서 현재 좌표중에 제일 가까운 4개의 좌표만 sorting한 후 넘어가는
		// 방식으로 sorting
		int minIdx = -1;
		double distance;
		int currentX;
		int currentY;
		int comparedX;
		int comparedY;

		int tempX;
		int tempY;

		Coordinate changedSkelCoordi;

		for (int i = 0; i < skeleton.size() - 4; i++) {
			currentX = skeleton.get(i).getX();
			currentY = skeleton.get(i).getY();
			for (int j = 1; j <= 4; j++) {
				for (int k = i + j; k < skeleton.size(); k++) {
					comparedX = skeleton.get(k).getX();
					comparedY = skeleton.get(k).getY();
					distance = getDistance(currentX, currentY, comparedX, comparedY);
					if (min > distance) {
						min = distance;
						minIdx = k;
					}
				}

				tempX = skeleton.get(minIdx).getX();
				tempY = skeleton.get(minIdx).getY();
				changedSkelCoordi = skeleton.get(i + j);
				skeleton.get(minIdx).moveTo(changedSkelCoordi.getX(), changedSkelCoordi.getY());
				skeleton.get(i + j).moveTo(tempX, tempY);

				min = 99999999; // min 값 초기화

			}
		}
	}

	double getDistance(int x, int y, int a, int b) {
		return Math.sqrt((x - a) * (x - a) + (y - b) * (y - b));
	}

	double getDistance(Coordinate first, Coordinate second) {
		return getDistance(first.getX(), first.getY(), second.getX(), second.getY());
	}

	int getNearSkelIdx(int polyIdx) {
		Coordinate polyCoordi = polyCoordiList.get(polyIdx);
		int x = polyCoordi.getX();
		int y = polyCoordi.getY();

		double minDistance = 999999999;
		int skelIdx = -1;
		int i = 0;
		for (Object o : skeleton) {
			Coordinate c = (Coordinate) o;
			if (minDistance > getDistance(x, y, c.getX(), c.getY())) {
				minDistance = getDistance(x, y, c.getX(), c.getY());
				skelIdx = i;
			}

			i++;
		}

		return skelIdx;

	}

	public void pairing() { // 자동으로 페어링을 해주는 함수
		int polyLength = polyCoordiList.size();
		int nearSkelArray[] = new int[polyLength]; // 각 꼭지점에서 가장 가까운 뼈대를 담는 배열
		int prePairArray[] = new int[polyLength]; //

		for (int i = 0; i < polyLength; i++)
			nearSkelArray[i] = getNearSkelIdx(i);

		System.out.print("가장 가까운 뼈대 나열:");
		for (int i = 0; i < nearSkelArray.length; i++)
			System.out.print(nearSkelArray[i] + ", ");
		System.out.println("");

		for (int i = 0; i < nearSkelArray.length; i++) { // 뼈대의 거리를 비교해서 가장 가까운
			// 뼈대를 가진 좌표를 찾음ㅁ
			double min = 999999999;
			int idx = -1;
			for (int j = 0; j < nearSkelArray.length; j++) {
				if (i == j)

					continue;

				int x1 = skeleton.get(nearSkelArray[i]).getX();
				int y1 = skeleton.get(nearSkelArray[i]).getY();
				int x2 = skeleton.get(nearSkelArray[j]).getX();
				int y2 = skeleton.get(nearSkelArray[j]).getY();
				double distance = getDistance(x1, y1, x2, y2);
				if (min > distance) {
					min = distance;
					idx = j;
				}
			}
			prePairArray[i] = idx;
		}

		System.out.print("예비 pair 나열:");
		for (int i = 0; i < prePairArray.length; i++) {
			System.out.print(prePairArray[i] + ",");
		}

		// 서로 짝이 맞으면 pair 생성 후

		for (int i = 0; i < prePairArray.length; i++) {
			if (prePairArray[i] == -1) { // 이미 처리가 되었다면
				continue;
			}

			int pair = prePairArray[i];
			if (prePairArray[pair] == i) {// 서로 짝이 맞으면
				// pairList.add(new Pair(polyCoordiList.get(i),
				// polyCoordiList.get(pair)));
				addPair(new Pair(i, pair, polyCoordiList));
				prePairArray[i] = -1;
				prePairArray[pair] = -1;
			}
		}

		for (int i = 0; i < prePairArray.length; i++) { // 페어링이 안된 점들을 리스트에 넣음
			if (prePairArray[i] != -1)
				notPairList.add(polyCoordiList.get(i));
		}

		for (int i = 0; i < pairList.size(); i++) {
			Pair pair = pairList.get(i);
			p.add(pair.getLeftLabel());
			p.add(pair.getRightLabel());

		}

		p.repaint();

	}
	// 이벤트 리스너 연동을 하려고 JLabel보다 상위 클래스인 Component를 사용
	public Coordinate idfCoordiFromButton(Component button) {
		try {
			for (int i = 0; i < polyCoordiList.size(); i++) {
				if (button == polyCoordiList.get(i).getButtonImg()) {
					return polyCoordiList.get(i);
				}
			}
			throw new Exception("주어진 버튼과 같은 Coordinate가 존재하지 않음");
		} catch (Exception e) {
			e.getMessage();
			return null;
		}
	}

	public Pair idfPairFromButton(Component button){
		Coordinate c = idfCoordiFromButton(button);
		try{
			for(Pair p:pairList){
				if(p.getLeft() == c || p.getRight() == c)
					return p;
			}
			throw new Exception("주어진 버튼이 속한 Pair를 찾을 수 없음");
		} catch(Exception e){
			e.getMessage();
			return null;
		}
	}

	public void startChangePairMode(Component button){
		changePairButton = button;

		if(isPairChangeMode()) //두번째 클릭일 때
		{
			System.out.println("이미 PairChangeMode입니다.");

		}

		changePairFlag = true;
	}

	public boolean isPairChangeMode(){
		return changePairFlag;
	}
	public boolean getGenPairFlag(){
		return genPairFlag;
	}
	public void abortChangePairMode(){
		System.out.println("change pair 모드 끝");
		changePairFlag = false;
		changePairButton = null;
	}


	public void genPair(Component button) { // 수동으로 페어링을 해주는 함수 ,  label만 출력해주면 될듯

		if (genPairFlag) // 이미 한개가 선택되었던 상황
		{
			genPairArr[1] = button;

			// 페어를 생성해서 리스트에 추가함
			Pair newPair = new Pair(idfCoordiFromButton(genPairArr[0]), idfCoordiFromButton(genPairArr[1]));
			//pair생성자가 자동으로 페어링된 Pair 객체 생성자와 다르다 -> left, right idx 초기화가 안됨.
			addPair(newPair);
			//초기화
			genPairArr[0] = null;
			genPairArr[1] = null;
			genPairFlag = false;
			abortChangePairMode();

			System.out.println("버튼 두 개 선택완료 페어가 추가됨");
			// UI 에 추가
			// 원래 빨간색으로 되어있던 버튼을 지우고 파란색으로 바꿔야함->일단 둘 다 지우는걸로 만듦

			p.remove(newPair.getLeft().getButtonImg());
			p.remove(newPair.getRight().getButtonImg());

			p.add(newPair.getLeftLabel());
			p.add(newPair.getRightLabel());

			JLabel leftButton = printButtonImg(newPair.getLeft(), "blue");
			JLabel rightButton = printButtonImg(newPair.getRight(), "blue");

			newPair.getLeft().setButtonImg(leftButton);
			newPair.getRight().setButtonImg(rightButton);
			leftButton.addMouseListener(new PairingMouseAdapter(this));
			rightButton.addMouseListener(new PairingMouseAdapter(this));

			p.repaint();

		} else { // 처음 선택된 상황
			genPairArr[0] = button;
			genPairFlag = true;
			System.out.println("버튼하나 선택");
		}

	}
	public void genPair(){
		genPair(changePairButton);
	}

	public void removePair(){
		if(changePairFlag){
			//눌러진 버튼의 pair를 찾아서
			Pair pair = idfPairFromButton(changePairButton);
			removeFromPanel(pair);// 화면상에서 지우고
			//빨간 버튼으로 새로 출력함
			JLabel leftButton = printButtonImg(pair.getLeft(), "red");
			JLabel rightButton = printButtonImg(pair.getRight(), "red");
			pair.getLeft().setButtonImg(leftButton);
			pair.getRight().setButtonImg(rightButton);

			//없어지는 부분.. 무엇이 없어지나...?
			//추가된 버튼의 마우스 리스너 등록
			leftButton.addMouseListener(new PairingMouseAdapter(this));
			rightButton.addMouseListener(new PairingMouseAdapter(this));
			//리스트에서 삭제
			pairList.remove(pair);
			p.repaint();
			abortChangePairMode();
		}
	}
	public void switchLR(){ //버튼이 오는 것이 아니라

		if(changePairFlag){
			Pair pair = idfPairFromButton(changePairButton);
			removeFromPanel(pair);

			pair.switchLR();
			printOnPanel(pair);
			p.repaint();
			abortChangePairMode();

		}
		else
		{
			System.out.println("changePair모드가 아닌데 switch 요구");
		}

	}
	public void removeFromPanel(Pair pair){  //화면상에서 지우는 부분
		p.remove(pair.getLeft().getButtonImg()); // 화면상에서 버튼 삭제
		p.remove(pair.getRight().getButtonImg());

		//polyButtonList.remove(pair.getLeft().getButtonImg());  //고쳐야함
		//polyButtonList.remove(pair.getRight().getButtonImg());
		p.remove(pair.getLeftLabel());  //화면상에서 label삭제
		p.remove(pair.getRightLabel());
	}

	public void printOnPanel(Pair pair){
		p.add(pair.getLeftLabel());
		p.add(pair.getRightLabel());

		//polyButtonList.add(pair.getLeft().getButtonImg());
		//polyButtonList.add(pair.getRight().getButtonImg());

		p.add(pair.getLeft().getButtonImg());
		p.add(pair.getRight().getButtonImg());
	}

	//pair를 리스트에 추가하는 함수 pair를 추가할때는 항상 이 함수를 통해 추가한다.
	public void addPair(Pair pair) {
		pair.setPairNum(++pairCount); // pairNum 초기화 // 이렇게 하면 중간에 없앴을 때 문제가됨..
		pair.setLabel();                                   //
		pairList.add(pair);
	}

	public void skeletonizing(int mtx[][]) {
		boolean isChange = true;

		int max = 0;
		while (isChange) { // 수정사항이 없을 때 까지 거리계산을 한다

			isChange = false;
			for (int i = 0; i < 1000; i++) {
				if (i == 0 || i == 999) // 모서리 스킵
					continue;
				for (int j = 0; j < 1000; j++) {
					if (j == 0 || j == 999 || mtx[i][j] == 0) // 모서리 스킵, 0스킵
						continue;
					int min = 10000;
					for (int k = i - 1; k <= i + 1; k++) { // 주변에서 가장 작은값 찾음
						for (int l = j - 1; l <= j + 1; l++) {
							if (mtx[k][l] < min)
								min = mtx[k][l];
						}
					}

					if (mtx[i][j] != min + 1) {
						isChange = true;
						mtx[i][j] = min + 1;

						if (max < mtx[i][j] && poly.inside(i, j)) // 외각선과 가장 먼
							// 거리
							max = mtx[i][j];
					}
				}
			}
		}
	}

	public void findGarbageSkel() { // 삐져나오는 쓰레기 좌표값들을 찾아내는 함수임
		garbageCoordiList = new ArrayList<Integer>();

		double distance;

		System.out.print("쓰레기좌표값: ");
		for (int i = 0; i < skeleton.size(); i++) { // 모든 뼈대 좌표에서 검색
			for (int j = 0; j < polyCoordiList.size(); j++) {

				distance = getDistance(skeleton.get(i), polyCoordiList.get(j)); // 뼈대
				//좌표와 외각선 꼭지점 사이의 거리를 구한다.
				if (distance < 9) {
					// printButtonImg(skeleton.get(i));
					System.out.println(
							"index: " + i + ",polyIdx: " + j + ", distance: " + Math.round(distance * 100) / 100.0);
					int endIdx = findEndIdx(i, j); // 하나 찾을 때 마다 지우는 과정을 거친 다음
					// 다음것을 찾아야한다.
					deleteGarbageSkel(endIdx);
					i = 0;
					j = 0;

					if (!garbageCoordiList.contains(endIdx)) // test
						garbageCoordiList.add(endIdx);
					break;
				}
			}
			break; // 하나만 찾고 나가게 하는 break
		}
		for (int i = 0; i < garbageCoordiList.size(); i++) {
			System.out.print(garbageCoordiList.get(i) + ", ");
		}
		System.out.println("\n쓰레기 좌표 수: " + garbageCoordiList.size());

	}

	public int findEndIdx(int skelIdx, int polyIdx) { // 입력받은 인덱스를 포함하는 뼈대의 끝
		// 부분을 찾아주는 함수
		if (skelIdx == 1639)
			System.out.println("test");
		// 경계조건 확인해야함..
		double currentDistance = getDistance(skeleton.get(skelIdx), polyCoordiList.get(polyIdx));
		double preDistance = 999999999;
		double nextDistance = 999999999;
		if (skelIdx != 0)
			preDistance = getDistance(skeleton.get(skelIdx - 1), polyCoordiList.get(polyIdx));
		if (skelIdx != skeleton.size() - 1)
			nextDistance = getDistance(skeleton.get(skelIdx + 1), polyCoordiList.get(polyIdx));

		if (currentDistance < preDistance && currentDistance < nextDistance) // 현재
			// 뼈대가
			// 외각선과
			// 가장
			// 가까운
			// 경우
			return skelIdx;
		else if (preDistance < nextDistance)
			return findEndIdx(skelIdx - 1, polyIdx);
		else
			return findEndIdx(skelIdx + 1, polyIdx);

	}

	// public void deleteGarbageSkel(int idx){ //쓰레기 뼈대의 끝부분을 받아서 이어진 쓰레기 뼈대들을
	// 모두 삭제하는 함수이다.
	// //쓰레기 뼈대는 좌표의 밀도가 낮은 특징을 사용한다.
	// //입력받은 뼈대 좌표의 인덱스가 가장 끝부분에 해당하는지 확인
	// Coordinate preSkel, nextSkel;
	// Coordinate currentSkel = skeleton.get(idx);
	// int direction = 0; //1이면 정방향 ,-1이면 역방향
	//
	// if(idx !=0 && idx != skeleton.size()-1){ //뼈대가 삭제될 방향을 정함
	// preSkel = skeleton.get(idx-1);
	// nextSkel = skeleton.get(idx+1);
	// if(getDistance(preSkel, currentSkel) < getDistance(nextSkel,
	// currentSkel))
	// direction = -1;
	// else
	// direction = 1;
	// }
	// else if(idx == 0) // 입력된 인덱스가 0인 경우 앞으로 리스트의 뒷쪽으로 이동하면서 삭제한다
	// direction = 1;
	// else //마지막인 경우 앞쪽으로~
	// direction = -1;
	//
	//
	// ////////////지우는 부분///////////////
	// int i = idx;
	// double distance;
	// boolean isGarbageEnd = true;
	// System.out.println("---------뼈대좌표거리---------");
	//
	// while(i >= 0 && i < skeleton.size()){
	// currentSkel = skeleton.get(i);
	// if(i == 0 && direction == -1) //끝에 도달
	// break;
	// if((i == skeleton.size() && direction == 1)) //끝에 도달
	// break;
	// // 다음 3개의 좌표를 검사해서 3개 이상 이어져 있으면 쓰레기 뼈대가 끝나는 것으로 간주
	// // 서로 거리가 2 이상인 좌표가 연속적으로 배열되어 있으면 쓰레기 좌표로 간주한다.
	// for(int j=1; j <= 3; j++){
	// nextSkel = skeleton.get(i + (direction*j));
	// distance = getDistance(currentSkel, nextSkel);
	// if(distance > 2)
	// //쓰레기 좌표임 삭제 하는 과정이 들어가야함
	// //정방향이면 그냥 삭제 역방향이면 하나씩 앞당기면서 삭제해야한다
	// {
	//
	// if(direction == 1){ // 정방향
	// for(int k=0; k<j; k++){
	// skeleton.remove(i);
	//
	// }
	// }
	//
	// else{ ///역방향
	// for(int k=0; k<j; k++){
	// skeleton.remove(i--);
	// }
	// }
	// isGarbageEnd = false;
	// break;
	// }
	//
	// if(j == 3) { // 쓰레기 좌표가 끝난것임 함수를 종료해야함
	// isGarbageEnd = true;
	// break;
	// }
	// }
	//
	// if(isGarbageEnd) {// 끝난경우
	//
	// break;
	// }
	// else {
	// continue;
	// }
	// }
	// }

	// -> 쓰지 않는 이유 : 쓰레기 뼈대로 인식하는데 밀도가 높으면 무한루프가 돈다~

	public void deleteGarbageSkel(int idx) { // 쓰레기 좌표의 끝 idx를 전달받아서 쓰레기 좌표가 끝날때
		// 까지 삭제를 한다 기울기 버젼

		// 입력받은 뼈대 좌표의 인덱스가 가장 끝부분에 해당하는지 확인
		Coordinate preSkel, nextSkel;
		Coordinate currentSkel = skeleton.get(idx);
		int idxDirection = 0; // 1이면 정방향 ,-1이면 역방향

		if (idx != 0 && idx != skeleton.size() - 1) { // 뼈대가 삭제될 방향을 정함
			preSkel = skeleton.get(idx - 1);
			nextSkel = skeleton.get(idx + 1);
			//끝의 좌표가 들어오기 때문에 거리가 더 가까우면 방향으로 함
			if (getDistance(preSkel, currentSkel) < getDistance(nextSkel, currentSkel))
				idxDirection = -1;
			else
				idxDirection = 1;
		} else if (idx == 0) // 입력된 인덱스가 0인 경우 앞으로 리스트의 뒷쪽으로 이동하면서 삭제한다
			idxDirection = 1;
		else // 마지막인 경우 앞쪽으로~
			idxDirection = -1;

		/////////////////
		int x1, x2;
		double y1, y2;
		double slope1 = 0;
		double slope2 = 0;
		int gap = 3;
		int idx1 = idx;
		int idx2 = idx1 + gap;
		int sum = 0;

		boolean isFind_1 = true;
		boolean isFind_2 = false;
		boolean isFirstSeek = true;

		ArrayList<Integer> indexList1 = new ArrayList<Integer>();
		ArrayList<Integer> indexList2 = new ArrayList<Integer>();

		indexList1.add(idx); // 처음좌표 입력

		x1 = skeleton.get(idx).getX(); // 초기값을 처음 들어온 인덱스의 좌표로 초기화
		y1 = skeleton.get(idx).getY();

		int faultCnt = 0;
		int xDirection;
		int searchIdx = idx;
		int preSearchIdx = 0;
		if (skeleton.get(idx1 + (idxDirection * gap)).getX() - skeleton.get(idx1).getX() > 0) // x좌표의
			// 방향을
			// 정함
			xDirection = 1;
		else if (skeleton.get(idx1 + (idxDirection * gap)).getX() - skeleton.get(idx1).getX() < 0)
			xDirection = -1;
		else {
			System.out.println("탐색할 x방향이 안나옴");
			xDirection = 0;
		}
		// 여기부터 두번째 x값을 가진 좌표들을 묶는 부분
		while (true) {
			// 기울기 체크해서 x값을 올리면서 탐색해나갈것인지 내리면서 할 것인지를 결정

			x2 = x1 + (xDirection * gap);

			// 기본으로 x 좌표가 gap 차이로 앞or뒤 에 있는 뼈대들을 검색하고 발견하지 못하면
			// 하나씩 탐색한다.
			while (!isFind_2) { // 다음 기울기를 비교할 idx 들을 골라낸다.
				int searchCnt = 1;
				while (searchCnt < gap + 8) {

					preSearchIdx = idx1 + (idxDirection * searchCnt);
					if (preSearchIdx >= skeleton.size()) {
						System.out.println("좌표가 끝남으로 인한 탐색 완료");
						System.out.println(idx + "to" + (skeleton.size() - 1));
						removeSkel(idx, skeleton.size() - 1);
						return;

					}
					if (getDistance(skeleton.get(preSearchIdx), skeleton.get(searchIdx)) > 50) {
						System.out.println("좌표가 튐으로 인한 탐색 완료");
						System.out.println(idx + "to" + preSearchIdx);
						removeSkel(idx, preSearchIdx);
						return;
					} else if ((skeleton.get(preSearchIdx).getX() - skeleton.get(searchIdx).getX()) * xDirection < 0) {
						System.out.println("case2,4 에 의한 탐색 완료");
						System.out.println(idx + "to" + preSearchIdx);
						removeSkel(idx, preSearchIdx);
						return;
					}

					else {
						searchIdx = preSearchIdx;
					}

					if (searchIdx > 0 && searchIdx < skeleton.size()) {
						if (skeleton.get(searchIdx).getX() == x2) {
							indexList2.add(searchIdx);
						}

						searchCnt++;
					}

					else {
						System.out.println("인덱스에러");
					}
					//
				}
				if (indexList2.size() > 0) // 찾은경우
					isFind_2 = true;
				else { // 못찾은경우

					x2 += xDirection; // x값이 증가할수도 있고 감소할 수도 있다. 그래서 무조건 더하면 안되고
					// 기울기를 보고 판단해야한다.
					faultCnt++;
					if (faultCnt > gap) { // x값을 조정해서 못찾는 경우에는 idx값을 조정해서 찾는것을
						// 시도한다 수정필요
						idx1 += 1;
						faultCnt = 0;
					}
					searchIdx = idx1; // 탐색범위 끝까지 갔던 searchIdx 값을 다시 처음으로 초기화
				}

			}

			sum = 0;
			for (int i = 0; i < indexList2.size(); i++) { // 리스트에 들어온 좌표에서 y값의
				// 평균을 낸다
				sum += skeleton.get(indexList2.get(i)).getY();
				idx1 = indexList2.get(i);
			}

			y2 = sum / (double) indexList2.size();

			slope2 = (y2 - y1) / (x2 - x1);
			if (isFirstSeek) { // 처음 탐색하는 경우에는 전에 나왔던 기울기와 비교불가하기때문에 그대로 넣어준다.
				isFirstSeek = false;
				slope1 = slope2;
			}

			if (Math.abs(slope2 - slope1) > 0.7) { // 기울기의 차이가 급격하게 변하면 탐색을 끝낸다.
				removeSkel(idx, searchIdx);
				return;
			}

			System.out.println("slope: " + slope1 + "인덱스: " + (idx1) + " x1:" + x1 + " x2:" + x2);

			x1 = x2;
			y1 = y2;

			x2 = -1;
			y2 = -1;

			isFind_2 = false;

			for (int i = 0; i < indexList2.size(); i++) // 계산이 끝났으므로 리스트에 있던
			// 좌표들을 다 지움
			{
				idx1 = indexList2.get(i); // 탐색된 좌표중에 가장 마지막을 저장
				indexList2.remove(i);

			}
			searchIdx = idx1; // 탐색범위 끝까지 갔던 searchIdx 값을 다시 처음으로 초기화
		}
	}

	public void removeSkel(int from, int to) {
		if (from == to)
			return;
		if (from > to) {
			int tmp;
			tmp = from;
			from = to;
			to = tmp;
		}

		JLabel[] testArray = printButtonImg(skeleton.get(from), skeleton.get(to));
		testArray[0].addMouseListener(new PairingMouseAdapter(this));
		testArray[1].addMouseListener(new PairingMouseAdapter(this));

		int cnt = to - from;
		while (cnt > 0) {
			skeleton.remove(from);
			cnt--;
		}

		System.out.println(from + " 부터 " + to + " 까지 삭제됨");
		// skeletonTest(100);

		return;

	}

	JLabel printButtonImg(Coordinate c1, String color) { // 버튼 이미지를 찍고 해당 이미지
		// 레이블을 반환한다.

		int x = c1.getX();
		int y = c1.getY();
		ImageIcon buttonIcon;
		String imgFileName;
		imgFileName = color + "Button.jpg";

		ImageIcon ButtonIcon = new ImageIcon(imgFileName);
		// ImageIcon redButton = new ImageIcon("redButton.jpg");
		JLabel buttonLabel = new JLabel("", ButtonIcon, JLabel.CENTER);
		buttonLabel.setHorizontalAlignment(JLabel.RIGHT);
		buttonLabel.setHorizontalTextPosition(JLabel.CENTER);
		buttonLabel.setLocation(x, y);
		buttonLabel.setSize(15, 15);

		p.add(buttonLabel);
		p.repaint();
		return buttonLabel;
	}

	JLabel[] printButtonImg(Coordinate c1, Coordinate c2) { // 이미지를 찍고 해당 이미지
		// 레이블 배열을 반환한다.

		// int x1 = c1.getX();
		// int y1 = c1.getY();
		//
		// int x2 = c2.getX();
		// int y2 = c2.getY();
		//
		// ImageIcon blueButton = new ImageIcon("blueButton.jpg");
		// ImageIcon redButton = new ImageIcon("redButton.jpg");
		// JLabel blue = new JLabel("", blueButton, JLabel.CENTER);
		// blue.setHorizontalAlignment(JLabel.RIGHT);
		// blue.setHorizontalTextPosition(JLabel.CENTER);
		// blue.setLocation(x1, y1);
		// blue.setSize(15, 15);
		//
		// JLabel red = new JLabel("", redButton, JLabel.CENTER);
		// red.setHorizontalAlignment(JLabel.RIGHT);
		// red.setHorizontalTextPosition(JLabel.CENTER);
		// red.setLocation(x2, y2);
		// red.setSize(15, 15);
		//
		//
		// p.add(blue);
		// p.add(red);
		// p.repaint();

		JLabel labelArray[] = new JLabel[2];
		labelArray[0] = printButtonImg(c1, "blue");
		labelArray[1] = printButtonImg(c2, "red");
		return labelArray;
	}

	public void printPairList() {
		for (int i = 0; i < pairList.size(); i++) {
			pairList.get(i).printPairList();
		}
	}
/*	// 라벨 추가
	public void create_form(Component cmpt, int x, int y, int w, int h){
	  GridBagConstraints gbc = new GridBagConstraints();
	  gbc.fill = GridBagConstraints.BOTH;
	  gbc.gridx = x;
	  gbc.gridy = y;
	  gbc.gridwidth = w;
	  gbc.gridheight = h;
	  this.Gbag.setConstraints(cmpt, gbc);
	  jp_label.add(cmpt);
	  jp_label.updateUI();
	}
	*/
	/*public static void main(String[] args) {
		// TODO Auto-generated method stub

		ArrayList<LetterPairing> lpList = new ArrayList<LetterPairing>();
		JFrame frame ; // 창
		JPanel p ;
		JScrollPane scroll;
		JButton completeButton = new JButton("적용 완료");
		completeButton.setLocation(100, 200);
		frame = new JFrame("LetterPairing"); // 창

		UfoIO curUfo;

		p = new JPanel() { // 화면에 뼈대 좌표들이 찍히게 만듦
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.BLACK);
				//g.drawPolygon(poly); // 글자의 외각선 표시
				for(LetterPairing lp:lpList){
					g.drawPolygon(lp.poly); //lplist에 있는 도형을 전부 그림
				}


				int max = 0;
				for (int i = 0; i < 1000; i++) { // 내부 점으로 찍음
					if (i == 0 || i == 999) // 모서리 스킵
						continue;
					for (int j = 0; j < 1000; j++) {
						if (j == 0 || j == 999 || matrix[i][j] == 0) // 모서리 스킵,
																		// 0스킵
							continue;

						max = 0; // 주변에서 가장 큰 값
						for (int k = i - 1; k <= i + 1; k++) { // 주변에서 가장 큰 값 찾음

							for (int l = j - 1; l <= j + 1; l++) { // max값을 찾음
								if (k == i && j == l)
									continue;
								if (matrix[k][l] > max)
									max = matrix[k][l];
							}
						}

						if (matrix[i][j] >= max) // 뼈대가 맞으면 그린다. 수정필
							g.drawLine(i, j, i, j);
					}
				}



				for(LetterPairing lp : lpList){
					ArrayList<Pair> pairList = lp.getPairList();
					//left는 left끼리  right는 right 끼리 선으로 잇는다
					// 다음 페어와 이어주는 구조
					for(int j=0;j<pairList.size()-1; j++){
						Pair p1 = pairList.get(j);
						Pair p2 = pairList.get(j+1);
						g.setColor(Color.GREEN);
						g.drawLine(p1.getLeft().getX(), p1.getLeft().getY(),
								p2.getLeft().getX(), p2.getLeft().getY());

						g.setColor(Color.ORANGE);
						g.drawLine(p1.getRight().getX(), p1.getRight().getY(),
								p2.getRight().getX(), p2.getRight().getY());
					}

					for(int j = 0; j<pairList.size(); j++){
						Pair p = pairList.get(j);
						g.setColor(Color.GREEN);
						g.drawLine(p.getLeft().getX(), p.getLeft().getY(),
								p.getRight().getX(), p.getRight().getY());
					}
				}
			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(1200, 1500);
			}

		};
		p.add(completeButton); // 패널에 완료 버튼 추가

		p.setLayout(null);

		completeButton.setBounds(400, 900, 100, 50);

		frame = new JFrame("LetterPairing");
		frame.setSize(1200, 1000);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setContentPane(p);
		//p.addKeyListener(new PairingKeyAdapter()); // 키보드 리스너 추가
		//frame.pack();

		frame.addKeyListener(new PairingKeyAdapter());
		JLabel label = new JLabel("0");

		//패널에 스크롤을 추가한다
		scroll = new JScrollPane(p , ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setBounds(0,0,1180,980);    // 프레임에 스크롤패널의 위치를 정한다
		p.setBackground(Color.WHITE);

		//폴더에 있는 glif 리스트를 가져옴
		String glifFolderPath = "glif";
		File glifFolder = new File(glifFolderPath);
		File []fileList = glifFolder.listFiles();
		ArrayList<UfoIO> ufoList = new ArrayList<UfoIO>();

		for(File file: fileList){
			ufoList.add(new UfoIO(file));
			System.out.println(file.getName());
		}
		int fileIdx = 0;


		//액션리스너에 들어가야할 코드들~
		completeButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				curUfo = ufoList.get(++fileIdx);
			}
		});


		curUfo = ufoList.get(fileIdx);
		//파싱된 데이터로 페어링
		ArrayList<Letter> letterList = curUfo.getLetterList();

		// 여러 도형을 표현하기 위해서 list에 추가!
		for(Letter letter: letterList){
			lpList.add(new LetterPairing(letter, frame, p));
		}


		//lp.findGarbageSkel();

		// lp.skeletonTest(100);
		for(LetterPairing lp: lpList){
			lp.printPairList();
		}

		ArrayList<Pair> pairSumList = new ArrayList<Pair>();

		//lpList에 있는 pair들을 하나로 합침
		// 파일로 출력하기 전에 마지막 단계에서 실행해야함
		for(LetterPairing lp: lpList){
			pairSumList.addAll(lp.getPairList());
		}
		
		curUfo.writeMetaUfo(pairSumList);
		frame.setContentPane(scroll);
	}*/
}