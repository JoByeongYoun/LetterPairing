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
	public JFrame frame ; // â
	public JPanel p ;
	public Polygon poly; //
	private int matrix[][]; // ���븦 ���� ���� �ܰ������κ��� �Ÿ��� ���
	private ArrayList<Coordinate> skeleton; // ���� ��ǥ list
	private ArrayList<Coordinate> polyCoordiList; // �ܰ��� ��ǥ list
	ArrayList<Integer> garbageCoordiList;
	private ArrayList<Pair> pairList;
	private ArrayList<Coordinate> notPairList;
	private ArrayList<JLabel> polyButtonList;
	
	private boolean changePairFlag;
	private boolean genPairFlag; // �������� �� �����ϴ� �����϶� true
	private Component[] genPairArr;
	private Component changePairButton;

	private int si;

	LetterPairing(Letter letter, JFrame frame, JPanel p) { // ��������� ���� letter�� �����´�.

		// ������ �ʱ�ȭ
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
		

		for (int i = 0; i < letter.size(); i++) { // �ܰ��� ��ǥ �ʱ�ȭ
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
		
		
		skeletonizing(matrix); // �Ÿ����
		setSkeleton();
		
		p.repaint();
		pairing();
		skeletonSort();

		for (int i = 0; i < pairList.size(); i++) { // ���� ��ư �̹����� ǥ��
			pairList.get(i).getLeft().setButtonImg(printButtonImg(pairList.get(i).getLeft(), "blue"));
			pairList.get(i).getRight().setButtonImg(printButtonImg(pairList.get(i).getRight(), "blue"));
			pairList.get(i).getLeft().getButtonImg().addMouseListener(new PairingMouseAdapter(this));
			pairList.get(i).getRight().getButtonImg().addMouseListener(new PairingMouseAdapter(this));
		}

		for (int i = 0; i < notPairList.size(); i++) { // �� ���� ���� ��ư �̹����� ǥ��
			notPairList.get(i).setButtonImg(printButtonImg(notPairList.get(i), "red"));
			notPairList.get(i).getButtonImg().addMouseListener(new PairingMouseAdapter(this));
		}

		// for(int i=0; i<notPairList.size(); i++ ){ // �� ���� ���� ��ư �̹����� ����
		//
		// p.remove(notPairList.get(i).getButtonImg());
		// }
		
	}

	ArrayList<Pair> getPairList(){
		return this.pairList;
	}
	
	public void setSkeleton() { // ���� ����
		int max = 0;
		for (int i = 0; i < 1000; i++) { // ���� ������ ����
			if (i == 0 || i == 999) // �𼭸� ��ŵ
				continue;
			for (int j = 0; j < 1000; j++) {
				if (j == 0 || j == 999 || matrix[i][j] == 0) // �𼭸� ��ŵ, 0��ŵ
					continue;

				max = 0; // �ֺ����� ���� ū ��
				for (int k = i - 1; k <= i + 1; k++) { // �ֺ����� ���� ū �� ã��

					for (int l = j - 1; l <= j + 1; l++) { // max���� ã��
						if (k == i && j == l)
							continue;
						if (matrix[k][l] > max)
							max = matrix[k][l];
					}
				}

				if (matrix[i][j] >= max) // ���밡 ������ ��ǥ�� �ִ´�.
					skeleton.add(new Coordinate(i, j));

			}
		}
	}

	void skeletonTest(int percent) {
		JFrame f; // â
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

	void skeletonSort() { // ���븦 sorting �ϴ� �Լ�
		double min = 9999999; // ���� ��ǥ�ѿ��� ���� ��ǥ�߿� ���� ����� 4���� ��ǥ�� sorting�� �� �Ѿ��
								// ������� sorting
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

				min = 99999999; // min �� �ʱ�ȭ

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

	public void pairing() { // �ڵ����� ���� ���ִ� �Լ�
		int polyLength = polyCoordiList.size();
		int nearSkelArray[] = new int[polyLength]; // �� ���������� ���� ����� ���븦 ��� �迭
		int prePairArray[] = new int[polyLength]; //

		for (int i = 0; i < polyLength; i++)
			nearSkelArray[i] = getNearSkelIdx(i);

		System.out.print("���� ����� ���� ����:");
		for (int i = 0; i < nearSkelArray.length; i++)
			System.out.print(nearSkelArray[i] + ", ");
		System.out.println("");

		for (int i = 0; i < nearSkelArray.length; i++) { // ������ �Ÿ��� ���ؼ� ���� �����
															// ���븦 ���� ��ǥ�� ã����
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

		System.out.print("���� pair ����:");
		for (int i = 0; i < prePairArray.length; i++) {
			System.out.print(prePairArray[i] + ",");
		}

		// ���� ¦�� ������ pair ���� ��

		for (int i = 0; i < prePairArray.length; i++) {
			if (prePairArray[i] == -1) { // �̹� ó���� �Ǿ��ٸ�
				continue;
			}

			int pair = prePairArray[i];
			if (prePairArray[pair] == i) {// ���� ¦�� ������
				// pairList.add(new Pair(polyCoordiList.get(i),
				// polyCoordiList.get(pair)));
				addPair(new Pair(i, pair, polyCoordiList));
				prePairArray[i] = -1;
				prePairArray[pair] = -1;
			}
		}

		for (int i = 0; i < prePairArray.length; i++) { // ���� �ȵ� ������ ����Ʈ�� ����
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
	// �̺�Ʈ ������ ������ �Ϸ��� JLabel���� ���� Ŭ������ Component�� ���
	public Coordinate idfCoordiFromButton(Component button) { 
		try {
			for (int i = 0; i < polyCoordiList.size(); i++) {
				if (button == polyCoordiList.get(i).getButtonImg()) {
					return polyCoordiList.get(i);
				}
			}
			throw new Exception("�־��� ��ư�� ���� Coordinate�� �������� ����");
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
			throw new Exception("�־��� ��ư�� ���� Pair�� ã�� �� ����");
		} catch(Exception e){
			e.getMessage();
			return null;
		}
	}

	public void startChangePairMode(Component button){
		changePairButton = button;
		
		if(isPairChangeMode()) //�ι�° Ŭ���� ��
		{
			System.out.println("�̹� PairChangeMode�Դϴ�.");
			
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
		System.out.println("change pair ��� ��");
		changePairFlag = false;
		changePairButton = null;
	}
	
	
	public void genPair(Component button) { // �������� ���� ���ִ� �Լ� ,  label�� ������ָ� �ɵ�
		
		if (genPairFlag) // �̹� �Ѱ��� ���õǾ��� ��Ȳ
		{
			genPairArr[1] = button;

			// �� �����ؼ� ����Ʈ�� �߰���
			Pair newPair = new Pair(idfCoordiFromButton(genPairArr[0]), idfCoordiFromButton(genPairArr[1]));
			//pair�����ڰ� �ڵ����� ���� Pair ��ü �����ڿ� �ٸ��� -> left, right idx �ʱ�ȭ�� �ȵ�.
			addPair(newPair);
			 //�ʱ�ȭ
			genPairArr[0] = null;
			genPairArr[1] = null;
			genPairFlag = false;
			abortChangePairMode();
			
			System.out.println("��ư �� �� ���ÿϷ� �� �߰���");
			// UI �� �߰�
			// ���� ���������� �Ǿ��ִ� ��ư�� ����� �Ķ������� �ٲ����->�ϴ� �� �� ����°ɷ� ����
			
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

		} else { // ó�� ���õ� ��Ȳ
			genPairArr[0] = button;
			genPairFlag = true;
			System.out.println("��ư�ϳ� ����");
		}

	}
	public void genPair(){
		genPair(changePairButton);
	}
	
	public void removePair(){
		if(changePairFlag){
			//������ ��ư�� pair�� ã�Ƽ� 
			Pair pair = idfPairFromButton(changePairButton);
			removeFromPanel(pair);// ȭ��󿡼� �����
			//���� ��ư���� ���� �����
			JLabel leftButton = printButtonImg(pair.getLeft(), "red");
			JLabel rightButton = printButtonImg(pair.getRight(), "red");
			pair.getLeft().setButtonImg(leftButton);
			pair.getRight().setButtonImg(rightButton);
			
			//�������� �κ�.. ������ ��������...?
			//�߰��� ��ư�� ���콺 ������ ���
			leftButton.addMouseListener(new PairingMouseAdapter(this));
			rightButton.addMouseListener(new PairingMouseAdapter(this));
			//����Ʈ���� ����
			pairList.remove(pair);
			p.repaint();
			abortChangePairMode();
		}	
	}
	public void switchLR(){ //��ư�� ���� ���� �ƴ϶�
		
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
			System.out.println("changePair��尡 �ƴѵ� switch �䱸");
		}
				
	}
	public void removeFromPanel(Pair pair){  //ȭ��󿡼� ����� �κ�
		p.remove(pair.getLeft().getButtonImg()); // ȭ��󿡼� ��ư ����
		p.remove(pair.getRight().getButtonImg());
		
		//polyButtonList.remove(pair.getLeft().getButtonImg());  //���ľ���
		//polyButtonList.remove(pair.getRight().getButtonImg());
		p.remove(pair.getLeftLabel());  //ȭ��󿡼� label����
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
	
	//pair�� ����Ʈ�� �߰��ϴ� �Լ� pair�� �߰��Ҷ��� �׻� �� �Լ��� ���� �߰��Ѵ�.
	public void addPair(Pair pair) { 
		pair.setPairNum(++pairCount); // pairNum �ʱ�ȭ // �̷��� �ϸ� �߰��� ������ �� ��������..
		pair.setLabel();                                   //
		pairList.add(pair);
	}

	public void skeletonizing(int mtx[][]) {
		boolean isChange = true;

		int max = 0;
		while (isChange) { // ���������� ���� �� ���� �Ÿ������ �Ѵ�

			isChange = false;
			for (int i = 0; i < 1000; i++) {
				if (i == 0 || i == 999) // �𼭸� ��ŵ
					continue;
				for (int j = 0; j < 1000; j++) {
					if (j == 0 || j == 999 || mtx[i][j] == 0) // �𼭸� ��ŵ, 0��ŵ
						continue;
					int min = 10000;
					for (int k = i - 1; k <= i + 1; k++) { // �ֺ����� ���� ������ ã��
						for (int l = j - 1; l <= j + 1; l++) {
							if (mtx[k][l] < min)
								min = mtx[k][l];
						}
					}

					if (mtx[i][j] != min + 1) {
						isChange = true;
						mtx[i][j] = min + 1;

						if (max < mtx[i][j] && poly.inside(i, j)) // �ܰ����� ���� ��
																	// �Ÿ�
							max = mtx[i][j];
					}
				}
			}
		}
	}

	public void findGarbageSkel() { // ���������� ������ ��ǥ������ ã�Ƴ��� �Լ���
		garbageCoordiList = new ArrayList<Integer>();

		double distance;

		System.out.print("��������ǥ��: ");
		for (int i = 0; i < skeleton.size(); i++) { // ��� ���� ��ǥ���� �˻�
			for (int j = 0; j < polyCoordiList.size(); j++) {

				distance = getDistance(skeleton.get(i), polyCoordiList.get(j)); // ����
																				// ��ǥ��
																				// �ܰ���
																				// ������
																				// ������
																				// �Ÿ���
																				// ����
				if (distance < 9) {
					// printButtonImg(skeleton.get(i));
					System.out.println(
							"index: " + i + ",polyIdx: " + j + ", distance: " + Math.round(distance * 100) / 100.0);
					int endIdx = findEndIdx(i, j); // �ϳ� ã�� �� ���� ����� ������ ��ģ ����
													// �������� ã�ƾ��Ѵ�.
					deleteGarbageSkel(endIdx);
					i = 0;
					j = 0;

					if (!garbageCoordiList.contains(endIdx)) // test
						garbageCoordiList.add(endIdx);
					break;
				}
			}
			break; // �ϳ��� ã�� ������ �ϴ� break
		}
		for (int i = 0; i < garbageCoordiList.size(); i++) {
			System.out.print(garbageCoordiList.get(i) + ", ");
		}
		System.out.println("\n������ ��ǥ ��: " + garbageCoordiList.size());

	}

	public int findEndIdx(int skelIdx, int polyIdx) { // �Է¹��� �ε����� �����ϴ� ������ ��
														// �κ��� ã���ִ� �Լ�
		if (skelIdx == 1639)
			System.out.println("test");
		// ������� Ȯ���ؾ���..
		double currentDistance = getDistance(skeleton.get(skelIdx), polyCoordiList.get(polyIdx));
		double preDistance = 999999999;
		double nextDistance = 999999999;
		if (skelIdx != 0)
			preDistance = getDistance(skeleton.get(skelIdx - 1), polyCoordiList.get(polyIdx));
		if (skelIdx != skeleton.size() - 1)
			nextDistance = getDistance(skeleton.get(skelIdx + 1), polyCoordiList.get(polyIdx));

		if (currentDistance < preDistance && currentDistance < nextDistance) // ����
																				// ���밡
																				// �ܰ�����
																				// ����
																				// �����
																				// ���
			return skelIdx;
		else if (preDistance < nextDistance)
			return findEndIdx(skelIdx - 1, polyIdx);
		else
			return findEndIdx(skelIdx + 1, polyIdx);

	}

	// public void deleteGarbageSkel(int idx){ //������ ������ ���κ��� �޾Ƽ� �̾��� ������ �������
	// ��� �����ϴ� �Լ��̴�.
	// //������ ����� ��ǥ�� �е��� ���� Ư¡�� ����Ѵ�.
	// //�Է¹��� ���� ��ǥ�� �ε����� ���� ���κп� �ش��ϴ��� Ȯ��
	// Coordinate preSkel, nextSkel;
	// Coordinate currentSkel = skeleton.get(idx);
	// int direction = 0; //1�̸� ������ ,-1�̸� ������
	//
	// if(idx !=0 && idx != skeleton.size()-1){ //���밡 ������ ������ ����
	// preSkel = skeleton.get(idx-1);
	// nextSkel = skeleton.get(idx+1);
	// if(getDistance(preSkel, currentSkel) < getDistance(nextSkel,
	// currentSkel))
	// direction = -1;
	// else
	// direction = 1;
	// }
	// else if(idx == 0) // �Էµ� �ε����� 0�� ��� ������ ����Ʈ�� �������� �̵��ϸ鼭 �����Ѵ�
	// direction = 1;
	// else //�������� ��� ��������~
	// direction = -1;
	//
	//
	// ////////////����� �κ�///////////////
	// int i = idx;
	// double distance;
	// boolean isGarbageEnd = true;
	// System.out.println("---------������ǥ�Ÿ�---------");
	//
	// while(i >= 0 && i < skeleton.size()){
	// currentSkel = skeleton.get(i);
	// if(i == 0 && direction == -1) //���� ����
	// break;
	// if((i == skeleton.size() && direction == 1)) //���� ����
	// break;
	// // ���� 3���� ��ǥ�� �˻��ؼ� 3�� �̻� �̾��� ������ ������ ���밡 ������ ������ ����
	// // ���� �Ÿ��� 2 �̻��� ��ǥ�� ���������� �迭�Ǿ� ������ ������ ��ǥ�� �����Ѵ�.
	// for(int j=1; j <= 3; j++){
	// nextSkel = skeleton.get(i + (direction*j));
	// distance = getDistance(currentSkel, nextSkel);
	// if(distance > 2)
	// //������ ��ǥ�� ���� �ϴ� ������ ������
	// //�������̸� �׳� ���� �������̸� �ϳ��� �մ��鼭 �����ؾ��Ѵ�
	// {
	//
	// if(direction == 1){ // ������
	// for(int k=0; k<j; k++){
	// skeleton.remove(i);
	//
	// }
	// }
	//
	// else{ ///������
	// for(int k=0; k<j; k++){
	// skeleton.remove(i--);
	// }
	// }
	// isGarbageEnd = false;
	// break;
	// }
	//
	// if(j == 3) { // ������ ��ǥ�� �������� �Լ��� �����ؾ���
	// isGarbageEnd = true;
	// break;
	// }
	// }
	//
	// if(isGarbageEnd) {// �������
	//
	// break;
	// }
	// else {
	// continue;
	// }
	// }
	// }

	// -> ���� �ʴ� ���� : ������ ����� �ν��ϴµ� �е��� ������ ���ѷ����� ����~

	public void deleteGarbageSkel(int idx) { // ������ ��ǥ�� �� idx�� ���޹޾Ƽ� ������ ��ǥ�� ������
												// ���� ������ �Ѵ� ���� ����

		// �Է¹��� ���� ��ǥ�� �ε����� ���� ���κп� �ش��ϴ��� Ȯ��
		Coordinate preSkel, nextSkel;
		Coordinate currentSkel = skeleton.get(idx);
		int idxDirection = 0; // 1�̸� ������ ,-1�̸� ������

		if (idx != 0 && idx != skeleton.size() - 1) { // ���밡 ������ ������ ����
			preSkel = skeleton.get(idx - 1);
			nextSkel = skeleton.get(idx + 1);
			//���� ��ǥ�� ������ ������ �Ÿ��� �� ������ �������� ��
			if (getDistance(preSkel, currentSkel) < getDistance(nextSkel, currentSkel))
				idxDirection = -1;
			else
				idxDirection = 1;
		} else if (idx == 0) // �Էµ� �ε����� 0�� ��� ������ ����Ʈ�� �������� �̵��ϸ鼭 �����Ѵ�
			idxDirection = 1;
		else // �������� ��� ��������~
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

		indexList1.add(idx); // ó����ǥ �Է�

		x1 = skeleton.get(idx).getX(); // �ʱⰪ�� ó�� ���� �ε����� ��ǥ�� �ʱ�ȭ
		y1 = skeleton.get(idx).getY();

		int faultCnt = 0;
		int xDirection;
		int searchIdx = idx;
		int preSearchIdx = 0;
		if (skeleton.get(idx1 + (idxDirection * gap)).getX() - skeleton.get(idx1).getX() > 0) // x��ǥ��
																								// ������
																								// ����
			xDirection = 1;
		else if (skeleton.get(idx1 + (idxDirection * gap)).getX() - skeleton.get(idx1).getX() < 0)
			xDirection = -1;
		else {
			System.out.println("Ž���� x������ �ȳ���");
			xDirection = 0;
		}
		// ������� �ι�° x���� ���� ��ǥ���� ���� �κ�
		while (true) {
			// ���� üũ�ؼ� x���� �ø��鼭 Ž���س��������� �����鼭 �� �������� ����

			x2 = x1 + (xDirection * gap);

			// �⺻���� x ��ǥ�� gap ���̷� ��or�� �� �ִ� ������� �˻��ϰ� �߰����� ���ϸ�
			// �ϳ��� Ž���Ѵ�.
			while (!isFind_2) { // ���� ���⸦ ���� idx ���� ��󳽴�.
				int searchCnt = 1;
				while (searchCnt < gap + 8) {

					preSearchIdx = idx1 + (idxDirection * searchCnt);
					if (preSearchIdx >= skeleton.size()) {
						System.out.println("��ǥ�� �������� ���� Ž�� �Ϸ�");
						System.out.println(idx + "to" + (skeleton.size() - 1));
						removeSkel(idx, skeleton.size() - 1);
						return;

					}
					if (getDistance(skeleton.get(preSearchIdx), skeleton.get(searchIdx)) > 50) {
						System.out.println("��ǥ�� Ʀ���� ���� Ž�� �Ϸ�");
						System.out.println(idx + "to" + preSearchIdx);
						removeSkel(idx, preSearchIdx);
						return;
					} else if ((skeleton.get(preSearchIdx).getX() - skeleton.get(searchIdx).getX()) * xDirection < 0) {
						System.out.println("case2,4 �� ���� Ž�� �Ϸ�");
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
						System.out.println("�ε�������");
					}
					//
				}
				if (indexList2.size() > 0) // ã�����
					isFind_2 = true;
				else { // ��ã�����

					x2 += xDirection; // x���� �����Ҽ��� �ְ� ������ ���� �ִ�. �׷��� ������ ���ϸ� �ȵǰ�
										// ���⸦ ���� �Ǵ��ؾ��Ѵ�.
					faultCnt++;
					if (faultCnt > gap) { // x���� �����ؼ� ��ã�� ��쿡�� idx���� �����ؼ� ã�°���
											// �õ��Ѵ� �����ʿ�
						idx1 += 1;
						faultCnt = 0;
					}
					searchIdx = idx1; // Ž������ ������ ���� searchIdx ���� �ٽ� ó������ �ʱ�ȭ
				}

			}

			sum = 0;
			for (int i = 0; i < indexList2.size(); i++) { // ����Ʈ�� ���� ��ǥ���� y����
															// ����� ����
				sum += skeleton.get(indexList2.get(i)).getY();
				idx1 = indexList2.get(i);
			}

			y2 = sum / (double) indexList2.size();

			slope2 = (y2 - y1) / (x2 - x1);
			if (isFirstSeek) { // ó�� Ž���ϴ� ��쿡�� ���� ���Դ� ����� �񱳺Ұ��ϱ⶧���� �״�� �־��ش�.
				isFirstSeek = false;
				slope1 = slope2;
			}

			if (Math.abs(slope2 - slope1) > 0.7) { // ������ ���̰� �ް��ϰ� ���ϸ� Ž���� ������.
				removeSkel(idx, searchIdx);
				return;
			}

			System.out.println("slope: " + slope1 + "�ε���: " + (idx1) + " x1:" + x1 + " x2:" + x2);

			x1 = x2;
			y1 = y2;

			x2 = -1;
			y2 = -1;

			isFind_2 = false;

			for (int i = 0; i < indexList2.size(); i++) // ����� �������Ƿ� ����Ʈ�� �ִ�
														// ��ǥ���� �� ����
			{
				idx1 = indexList2.get(i); // Ž���� ��ǥ�߿� ���� �������� ����
				indexList2.remove(i);

			}
			searchIdx = idx1; // Ž������ ������ ���� searchIdx ���� �ٽ� ó������ �ʱ�ȭ
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

		System.out.println(from + " ���� " + to + " ���� ������");
		// skeletonTest(100);

		return;

	}

	JLabel printButtonImg(Coordinate c1, String color) { // ��ư �̹����� ��� �ش� �̹���
															// ���̺��� ��ȯ�Ѵ�.

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

	JLabel[] printButtonImg(Coordinate c1, Coordinate c2) { // �̹����� ��� �ش� �̹���
															// ���̺� �迭�� ��ȯ�Ѵ�.

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
/*	// �� �߰� 
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
		JFrame frame ; // â
		JPanel p ;
		JScrollPane scroll;
		JButton completeButton = new JButton("���� �Ϸ�");
		completeButton.setLocation(100, 200);
		frame = new JFrame("LetterPairing"); // â
		
		UfoIO curUfo;
		
		p = new JPanel() { // ȭ�鿡 ���� ��ǥ���� ������ ����
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.BLACK);
				//g.drawPolygon(poly); // ������ �ܰ��� ǥ�� 
				for(LetterPairing lp:lpList){
					g.drawPolygon(lp.poly); //lplist�� �ִ� ������ ���� �׸� 
				}

				
				int max = 0;
				for (int i = 0; i < 1000; i++) { // ���� ������ ����
					if (i == 0 || i == 999) // �𼭸� ��ŵ
						continue;
					for (int j = 0; j < 1000; j++) {
						if (j == 0 || j == 999 || matrix[i][j] == 0) // �𼭸� ��ŵ,
																		// 0��ŵ
							continue;

						max = 0; // �ֺ����� ���� ū ��
						for (int k = i - 1; k <= i + 1; k++) { // �ֺ����� ���� ū �� ã��

							for (int l = j - 1; l <= j + 1; l++) { // max���� ã��
								if (k == i && j == l)
									continue;
								if (matrix[k][l] > max)
									max = matrix[k][l];
							}
						}

						if (matrix[i][j] >= max) // ���밡 ������ �׸���. ������
							g.drawLine(i, j, i, j);
					}
				}
				
				
				
				for(LetterPairing lp : lpList){
					ArrayList<Pair> pairList = lp.getPairList();
					//left�� left����  right�� right ���� ������ �մ´�
					// ���� ���� �̾��ִ� ����
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
		p.add(completeButton); // �гο� �Ϸ� ��ư �߰�
		
		p.setLayout(null);
		
		completeButton.setBounds(400, 900, 100, 50);
				
		frame = new JFrame("LetterPairing");
		frame.setSize(1200, 1000);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setContentPane(p);
		//p.addKeyListener(new PairingKeyAdapter()); // Ű���� ������ �߰�
		//frame.pack();
		
		frame.addKeyListener(new PairingKeyAdapter());
		JLabel label = new JLabel("0");

		//�гο� ��ũ���� �߰��Ѵ�
		scroll = new JScrollPane(p , ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setBounds(0,0,1180,980);    // �����ӿ� ��ũ���г��� ��ġ�� ���Ѵ�
		p.setBackground(Color.WHITE);

		//������ �ִ� glif ����Ʈ�� ������
		String glifFolderPath = "glif";
		File glifFolder = new File(glifFolderPath);
		File []fileList = glifFolder.listFiles();
		ArrayList<UfoIO> ufoList = new ArrayList<UfoIO>();
		
		for(File file: fileList){
			ufoList.add(new UfoIO(file));
			System.out.println(file.getName());
		}
		int fileIdx = 0;
		
		
		//�׼Ǹ����ʿ� ������ �ڵ��~
		completeButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				curUfo = ufoList.get(++fileIdx);
			}
		});
		
		
		curUfo = ufoList.get(fileIdx);
		//�Ľ̵� �����ͷ� �� 
		ArrayList<Letter> letterList = curUfo.getLetterList();
		
		// ���� ������ ǥ���ϱ� ���ؼ� list�� �߰�!
		for(Letter letter: letterList){ 
			lpList.add(new LetterPairing(letter, frame, p));    
		}
			

		//lp.findGarbageSkel();

		// lp.skeletonTest(100);
		for(LetterPairing lp: lpList){
			lp.printPairList();
		}
		
		ArrayList<Pair> pairSumList = new ArrayList<Pair>();
		
		//lpList�� �ִ� pair���� �ϳ��� ��ħ
		// ���Ϸ� ����ϱ� ���� ������ �ܰ迡�� �����ؾ���
		for(LetterPairing lp: lpList){
			pairSumList.addAll(lp.getPairList());
		}
		
		curUfo.writeMetaUfo(pairSumList);
		frame.setContentPane(scroll);
	}*/
}