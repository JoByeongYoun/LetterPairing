import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.*;

public class PairingGui extends JFrame {
	
	//화면 요소들
	JFrame frame;
	JPanel p;
	JScrollPane scroll;
	JButton completeButton;
	PairingKeyAdapter pairingKeyAdapter;
	
	//데이터 요소들
	UfoIO curUfo;
	ArrayList<LetterPairing> lpList;
	int fileIdx;
	ArrayList<UfoIO> ufoList ;
	ArrayList<Letter> letterList;
	ArrayList<Pair> pairSumList;
	
	public PairingGui(){
		fileIdx = 0;
		lpList = new ArrayList<LetterPairing>();
		pairingKeyAdapter = new PairingKeyAdapter();
		completeButton = new JButton("적용 완료");
		completeButton.setLocation(100, 200);
		frame = this;
		p = new JPanel() { // 화면에 뼈대 좌표들이 찍히게 만듦
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.BLACK);
				//g.drawPolygon(poly); // 글자의 외각선 표시 
				for(LetterPairing lp:lpList){
					g.drawPolygon(lp.poly); //lplist에 있는 도형을 전부 그림 
				}

				
				/*int max = 0;
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
				}*/
				
				
				
				for(LetterPairing lp : lpList){
					ArrayList<Pair> pairList = lp.getPairList();
					//left는 left끼리  right는 right 끼리 선으로 잇는다
					// 다음 페어와 이어주는 구조
					/*for(int j=0;j<pairList.size()-1; j++){
						Pair p1 = pairList.get(j);
						Pair p2 = pairList.get(j+1);
						g.setColor(Color.GREEN);
						g.drawLine(p1.getLeft().getX(), p1.getLeft().getY(),
								p2.getLeft().getX(), p2.getLeft().getY());
						
						g.setColor(Color.ORANGE);
						g.drawLine(p1.getRight().getX(), p1.getRight().getY(),
								p2.getRight().getX(), p2.getRight().getY());
					}*/
					
					//짝끼리 파란색으로 이어준다.
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
		//다 수동으로 조절하기 위해
		p.setLayout(null);
	
		completeButton.setBounds(400, 900, 100, 50);
		this.setSize(1200, 1000);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		this.addKeyListener(pairingKeyAdapter);
		
			
		JLabel label = new JLabel("0");
		
		scroll = new JScrollPane(p , ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setBounds(0,0,1180,980);    // 프레임에 스크롤패널의 위치를 정한다
		p.setBackground(Color.WHITE);
		
		
		//폴더에 있는 glif 리스트를 가져옴
		// 7/28 준은 추가 ... 폴더 선택 다이얼로그 띄워서 선택 ...	
		// 7/31 준은 수정... 단일파일 혹은 폴더를 선택해서 페어링...
		
		// 8/1 준은 추가 ... 파일 혹은 폴더를 선택하라는 다이얼로그
		//JOptionPane.showMessageDialog(p,"glif 파일이나 폴더를 선택 하세요.");

		
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setMultiSelectionEnabled(true);

		int ret = chooser.showOpenDialog(null);
		
		String glifFolderPath = chooser.getSelectedFile().getPath() + "/glyphs";
		
		
		
		File glifFolderAndFile = new File(glifFolderPath);

		System.out.println(glifFolderPath);
	
		// 7/31 준은 : 파일을 선택한 경우와 폴더를 선택한 경우를 다르게 처리.
		// 폴더는 한꺼번에 전부 터리. 파일은 멀티파일 처리 가능
		if(glifFolderAndFile.isFile()) {	
			
			File []fileList = chooser.getSelectedFiles();
					
			ufoList = new ArrayList<UfoIO>();
			
			for(File file: fileList){
				ufoList.add(new UfoIO(file));
			}
			
		}
		else if(glifFolderAndFile.isDirectory()) {
			File []fileList = glifFolderAndFile.listFiles();
			
			ufoList = new ArrayList<UfoIO>();
			//폴더에 있는 파일들을 ufoList에 더해준다.
			for(File file: fileList){
				ufoList.add(new UfoIO(file));
			}
		}
		
		System.out.println("파일 선택 완료");
		

		fileIdx = 0;
		completeButton.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//meTaUfo 작성
				

				// 7/31 준은 : 파일을 모두 읽으면 프로그램 종료
				if(ufoList.size() < (fileIdx + 1)) {
					
					//다이얼로그를 띄운 후 프로그램 종료
					JOptionPane.showMessageDialog(p,"페어링을 완료했습니다. 종료합니다.");
					System.exit(0);
				}
				
				// 8/1 준은 : 현재 파일을 저장함 앞에서 fileIdx++을 먼저 해줬으므로 현재 인덱스 전의 것으로 저장해야 한다.
				curUfo.writeMetaUfo(pairSumList,ufoList.get(fileIdx-1).getUfoFile());
				
				curUfo = ufoList.get(fileIdx++);
				
				//리스트에 있는 글자를 지우고 다시 셋팅
				letterList.clear();
				letterList = curUfo.getLetterList();
				
				p.removeAll(); // panel 초기화  버튼들만 지워야하는데 귀찮아서 일단 이렇게 처리함
				p.add(completeButton);
				
				lpList.clear(); // lplist에 있는 내용을 다 지운 후
				// 여러 도형을 표현하기 위해서 list에 추가!
				for(Letter letter: letterList){ 
					lpList.add(new LetterPairing(letter, frame, p));    
				}
				
				pairingKeyAdapter.clear();
				for(LetterPairing lp : lpList){
					pairingKeyAdapter.addLp(lp);
				}
				pairSumList.clear();
				//lpList에 있는 pair들을 하나로 합침
				// 파일로 출력하기 전에 마지막 단계에서 실행해야함
				for(LetterPairing lp: lpList){
					pairSumList.addAll(lp.getPairList());
				}
				
				// 7/28 준은 수정 ... 글자 변경시 키보드 안먹던거 먹게함 ...
				frame.requestFocus();
				
			    //frame.addKeyListener(pairingKeyAdapter);
				//frame.getKeyListeners();
				System.out.println("");
				
			}
		});
		
		curUfo = ufoList.get(fileIdx++);

		//파싱된 데이터로 페어링 
		letterList = curUfo.getLetterList();
		
		// 여러 도형을 표현하기 위해서 list에 추가!
		for(Letter letter: letterList){ 
			lpList.add(new LetterPairing(letter, this, p));    
		}
		
		//리스트에 저장된 lp들을 수동 수정모드를 위해 keyAdapter에 추가해줌
		for(LetterPairing lp : lpList){
			pairingKeyAdapter.addLp(lp);
		}
			

		//lp.findGarbageSkel();

		// lp.skeletonTest(100);
		/*for(LetterPairing lp: lpList){
			lp.printPairList();
		}*/
		
		pairSumList = new ArrayList<Pair>();
		
		//lpList에 있는 pair들을 하나로 합침
		// 파일로 출력하기 전에 마지막 단계에서 실행해야함
		for(LetterPairing lp: lpList){
			pairSumList.addAll(lp.getPairList());
		}
		
		//curUfo.writeMetaUfo(pairSumList);
		this.setContentPane(scroll);
		
	}
}
