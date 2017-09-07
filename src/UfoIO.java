import java.io.*;
import java.util.*;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;
public class UfoIO {   //UFO���Ͽ��� �����͸� �����ϴ� Ŭ����
	private File ufoFile;
	private Letter letter;  //
	private ArrayList<Letter> letterList;
	private StringTokenizer tokenizer;
	
	private ArrayList<Integer> xList;
	private ArrayList<Integer> yList;
	// �Ľ̵� ���� �ѹ� ����, ���� �ѹ��� ������������ ����ȴ�.
	private ArrayList<Integer> lineNumList;  
	
	
	private String fileName;
	private String metaUfoStr ;
		
	public UfoIO(File ufoFile){
		this.ufoFile = ufoFile;
		this.fileName = ufoFile.getName();
		letterList = new ArrayList<Letter>();
		/*xList = new ArrayList<Integer>();  // �Ľ̵� x��ǥ ����
		yList = new ArrayList<Integer>();   // �Ľ̵� y��ǥ ����
		*/		
		lineNumList = new ArrayList<Integer>(); // �Ľ̵� ���� �ѹ� ����
		lineNumList.add(-1); // ó���� -1�� �־���
		extractData(ufoFile);
		
		//letter = new Letter(xList, yList);
	}
	public UfoIO(String fileName){
		this.fileName = fileName;
		this.ufoFile = ufoFile = new File(fileName);
		
		lineNumList = new ArrayList<Integer>(); // �Ľ̵� ���� �ѹ� ����
		lineNumList.add(-1); // ó���� -1�� �־���
		extractData(ufoFile);

	}
	
	public Letter getLetter(){
		if(letter == null)
			System.out.println("letter �ʱ�ȭ ����");
		return this.letter;
	}
	public ArrayList<Letter> getLetterList(){
		if(letterList == null){
			System.out.println("�Ľ̵� letter�� ����");
		}
		return letterList;
	}
	
	private int reverseY(int num){
		int standard = 500;
		return 2 * standard - num;
	}
	//glif ���Ͽ��� ��Ʈ�� �����͸� �Ľ��ϴ� �Լ���
	public void extractData(File ufoFile){
		
		//ufoFile = new File(fileName);
		if(ufoFile.exists()){
			try{
				FileReader fileReader = new FileReader(ufoFile);
				BufferedReader br = new BufferedReader(fileReader);
				String line = null;
				int i = 0;  // �� ���� ����.
				while((line = br.readLine()) != null){ //������ ������ ���� 
					if(line.contains("<contour>")){ // ���� �ϳ� ����
						
						xList = new ArrayList<Integer>();  // �Ľ̵� x��ǥ ����
						yList = new ArrayList<Integer>();   // �Ľ̵� y��ǥ ����
					}
					
					if(line.contains("</contour>")){ // ���� ��!
						letter = new Letter(xList, yList);
						letterList.add(letter);
						//xList, yList�� ��Ȱ�� �ؼ� ���� Letter�� ������ ���̱�  ������ null�� �ʱ�ȭ
						xList = null;
						yList = null;
					}
					if (line.contains("type=\"line\"") || line.contains("type=\"qcurve\"")){ // type�� �ִ� ����Ʈ�� ����
						
						String tokenline = line.substring(line.indexOf("x=\"")+3, line.length());;
						String intStr; // �߷��� ���� ���ڰ� �� ������
						tokenizer = new StringTokenizer(tokenline, "\"");
						if(tokenizer.hasMoreTokens()){
							intStr = tokenizer.nextToken();

							int x = Integer.parseInt(intStr); //x�� ����
							xList.add(x);
							
							tokenline = line.substring(line.indexOf("y=\"")+3, line.length());
							tokenizer = new StringTokenizer(tokenline, "\"");
							intStr = tokenizer.nextToken();
							int y = reverseY(Integer.parseInt(intStr));
							yList.add(y);
							
							lineNumList.add(i); //line �ѹ��� ����
						}
						else
							throw new Exception("StringTokenizer Problem");
					}

					i++;
				}
				br.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	// 7/31 ���� �߰� : GUI���� ������ �޾ƿͼ� �ҷ��� ������ �����ϴ� ������ �״�� �����.
	public void writeMetaUfo(ArrayList<Pair> pairList, File file){
		metaUfoStr = "";
		xList = new ArrayList<Integer>();  // �Ľ̵� x��ǥ ����
		yList = new ArrayList<Integer>();   // �Ľ̵� y��ǥ ����
		for(Letter l: letterList){
			for(int i=0;i<l.xArray.length;i++){
				xList.add(l.xArray[i]);
				yList.add(l.yArray[i]);
			}
		}
		try{
			//lineNumList�� ����� ���� �����ͼ� pair�� Ȯ���� �� ������ �߰����ش�
			if(ufoFile.exists()){
				FileReader fileReader = new FileReader(ufoFile);
				BufferedReader br = new BufferedReader(fileReader);
				String line = null;
				int lineNum = 0; // ���� ó������ line num
				
				while((line = br.readLine()) != null){
					if(lineNumList.size() > 1){ // �Ľ��� �����Ͱ� ���������� ó��
						if(lineNumList.get(1) == lineNum){ // ������ �����϶� pair ������ ã�Ƽ� ���ش�.
							
							//�ش� ���� �����Ͱ� ���� �Ǿ����� ã�´�.
							for(int i=0; i < pairList.size(); i++){ 
								Coordinate left = pairList.get(i).getLeft();
								
	 							if(left.getX() == xList.get(0) && left.getY() == yList.get(0)){
									line = line.replace("type", "penPair=\"z"+pairList.get(i).getPairNum()+"l\" type");
									//metaUfoStr = metaUfoStr + line + "\n";
									xList.remove(0);
									yList.remove(0);
									lineNumList.remove(1);
									break;
								}
								Coordinate right = pairList.get(i).getRight();
								if(right.getX() == xList.get(0) && right.getY() == yList.get(0)){
									line = line.replace("type", "penPair=\"z"+pairList.get(i).getPairNum()+"r\" type");
									//metaUfoStr = metaUfoStr + line + "\n";
									xList.remove(0);
									yList.remove(0);
									lineNumList.remove(1);
									break;
								}
								
								if(i == pairList.size()-1)
								{
									//pairList�� �� ���Ҵµ��� �ȳ����� ���� ������ ���̴�.
									//metaUfoStr = metaUfoStr + line + "\n";
									lineNumList.remove(1);
									xList.remove(0);
									yList.remove(0);
								}	
							}
						}
					}
				//	if(lineNumList.get(0) != lineNum){ // ������ ������ �ƴҶ��� �״�� ����Ѵ�.
						metaUfoStr = metaUfoStr + line + "\n";
					//}
					
					lineNum++;
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();	
		}

		try{
			String path = file.getPath();
			BufferedWriter writer = null;
			
			
			if(file.isDirectory()) {
				path = path + "/"+ fileName;	
			}
			else if(file.isFile()) {
				;
			}
			
			
			writer = new BufferedWriter(new FileWriter(path));						
			writer.write(metaUfoStr);
			writer.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public File getUfoFile() {
		return this.ufoFile;
	}
	
	
}
