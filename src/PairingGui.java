import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.*;

public class PairingGui extends JFrame {
	
	//ȭ�� ��ҵ�
	JFrame frame;
	JPanel p;
	JScrollPane scroll;
	JButton completeButton;
	PairingKeyAdapter pairingKeyAdapter;
	
	//������ ��ҵ�
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
		completeButton = new JButton("���� �Ϸ�");
		completeButton.setLocation(100, 200);
		frame = this;
		p = new JPanel() { // ȭ�鿡 ���� ��ǥ���� ������ ����
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.BLACK);
				//g.drawPolygon(poly); // ������ �ܰ��� ǥ�� 
				for(LetterPairing lp:lpList){
					g.drawPolygon(lp.poly); //lplist�� �ִ� ������ ���� �׸� 
				}

				
				/*int max = 0;
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
				}*/
				
				
				
				for(LetterPairing lp : lpList){
					ArrayList<Pair> pairList = lp.getPairList();
					//left�� left����  right�� right ���� ������ �մ´�
					// ���� ���� �̾��ִ� ����
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
					
					//¦���� �Ķ������� �̾��ش�.
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
		//�� �������� �����ϱ� ����
		p.setLayout(null);
	
		completeButton.setBounds(400, 900, 100, 50);
		this.setSize(1200, 1000);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		this.addKeyListener(pairingKeyAdapter);
		
			
		JLabel label = new JLabel("0");
		
		scroll = new JScrollPane(p , ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setBounds(0,0,1180,980);    // �����ӿ� ��ũ���г��� ��ġ�� ���Ѵ�
		p.setBackground(Color.WHITE);
		
		
		//������ �ִ� glif ����Ʈ�� ������
		// 7/28 ���� �߰� ... ���� ���� ���̾�α� ����� ���� ...	
		// 7/31 ���� ����... �������� Ȥ�� ������ �����ؼ� ��...
		
		// 8/1 ���� �߰� ... ���� Ȥ�� ������ �����϶�� ���̾�α�
		//JOptionPane.showMessageDialog(p,"glif �����̳� ������ ���� �ϼ���.");

		
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setMultiSelectionEnabled(true);

		int ret = chooser.showOpenDialog(null);
		
		String glifFolderPath = chooser.getSelectedFile().getPath() + "/glyphs";
		
		
		
		File glifFolderAndFile = new File(glifFolderPath);

		System.out.println(glifFolderPath);
	
		// 7/31 ���� : ������ ������ ���� ������ ������ ��츦 �ٸ��� ó��.
		// ������ �Ѳ����� ���� �͸�. ������ ��Ƽ���� ó�� ����
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
			//������ �ִ� ���ϵ��� ufoList�� �����ش�.
			for(File file: fileList){
				ufoList.add(new UfoIO(file));
			}
		}
		
		System.out.println("���� ���� �Ϸ�");
		

		fileIdx = 0;
		completeButton.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//meTaUfo �ۼ�
				

				// 7/31 ���� : ������ ��� ������ ���α׷� ����
				if(ufoList.size() < (fileIdx + 1)) {
					
					//���̾�α׸� ��� �� ���α׷� ����
					JOptionPane.showMessageDialog(p,"���� �Ϸ��߽��ϴ�. �����մϴ�.");
					System.exit(0);
				}
				
				// 8/1 ���� : ���� ������ ������ �տ��� fileIdx++�� ���� �������Ƿ� ���� �ε��� ���� ������ �����ؾ� �Ѵ�.
				curUfo.writeMetaUfo(pairSumList,ufoList.get(fileIdx-1).getUfoFile());
				
				curUfo = ufoList.get(fileIdx++);
				
				//����Ʈ�� �ִ� ���ڸ� ����� �ٽ� ����
				letterList.clear();
				letterList = curUfo.getLetterList();
				
				p.removeAll(); // panel �ʱ�ȭ  ��ư�鸸 �������ϴµ� �����Ƽ� �ϴ� �̷��� ó����
				p.add(completeButton);
				
				lpList.clear(); // lplist�� �ִ� ������ �� ���� ��
				// ���� ������ ǥ���ϱ� ���ؼ� list�� �߰�!
				for(Letter letter: letterList){ 
					lpList.add(new LetterPairing(letter, frame, p));    
				}
				
				pairingKeyAdapter.clear();
				for(LetterPairing lp : lpList){
					pairingKeyAdapter.addLp(lp);
				}
				pairSumList.clear();
				//lpList�� �ִ� pair���� �ϳ��� ��ħ
				// ���Ϸ� ����ϱ� ���� ������ �ܰ迡�� �����ؾ���
				for(LetterPairing lp: lpList){
					pairSumList.addAll(lp.getPairList());
				}
				
				// 7/28 ���� ���� ... ���� ����� Ű���� �ȸԴ��� �԰��� ...
				frame.requestFocus();
				
			    //frame.addKeyListener(pairingKeyAdapter);
				//frame.getKeyListeners();
				System.out.println("");
				
			}
		});
		
		curUfo = ufoList.get(fileIdx++);

		//�Ľ̵� �����ͷ� �� 
		letterList = curUfo.getLetterList();
		
		// ���� ������ ǥ���ϱ� ���ؼ� list�� �߰�!
		for(Letter letter: letterList){ 
			lpList.add(new LetterPairing(letter, this, p));    
		}
		
		//����Ʈ�� ����� lp���� ���� ������带 ���� keyAdapter�� �߰�����
		for(LetterPairing lp : lpList){
			pairingKeyAdapter.addLp(lp);
		}
			

		//lp.findGarbageSkel();

		// lp.skeletonTest(100);
		/*for(LetterPairing lp: lpList){
			lp.printPairList();
		}*/
		
		pairSumList = new ArrayList<Pair>();
		
		//lpList�� �ִ� pair���� �ϳ��� ��ħ
		// ���Ϸ� ����ϱ� ���� ������ �ܰ迡�� �����ؾ���
		for(LetterPairing lp: lpList){
			pairSumList.addAll(lp.getPairList());
		}
		
		//curUfo.writeMetaUfo(pairSumList);
		this.setContentPane(scroll);
		
	}
}
