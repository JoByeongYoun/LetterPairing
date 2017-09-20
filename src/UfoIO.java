import java.io.*;
import java.util.*;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;
public class UfoIO {   //UFO파일에서 데이터를 추출하는 클래스
	private File ufoFile;
	private Letter letter;  //
	private ArrayList<Letter> letterList;
	private StringTokenizer tokenizer;

	private ArrayList<Integer> xList;
	private ArrayList<Integer> yList;
	// 파싱된 라인 넘버 저장, 라인 넘버가 오름차순으로 저장된다.
	private ArrayList<Integer> lineNumList;


	private String fileName;
	private String metaUfoStr ;

	static int cnt = 0;

	public UfoIO(File ufoFile){
		this.ufoFile = ufoFile;
		this.fileName = ufoFile.getName();
		letterList = new ArrayList<Letter>();
		/*xList = new ArrayList<Integer>();  // 파싱된 x좌표 저장
		yList = new ArrayList<Integer>();   // 파싱된 y좌표 저장
		*/
		lineNumList = new ArrayList<Integer>(); // 파싱된 라인 넘버 저장
		lineNumList.add(-1); // 처음에 -1을 넣어줌
		extractData(ufoFile);

		//letter = new Letter(xList, yList);
	}
	public UfoIO(String fileName){
		this.fileName = fileName;
		this.ufoFile = ufoFile = new File(fileName);

		lineNumList = new ArrayList<Integer>(); // 파싱된 라인 넘버 저장
		lineNumList.add(-1); // 처음에 -1을 넣어줌
		extractData(ufoFile);

	}

	public Letter getLetter(){
		if(letter == null)
			System.out.println("letter 초기화 오류");
		return this.letter;
	}
	public ArrayList<Letter> getLetterList(){
		if(letterList == null){
			System.out.println("파싱된 letter가 없음");
		}
		return letterList;
	}

	private int reverseY(int num){
		int standard = 500;
		return 2 * standard - num;
	}
	//glif 파일에서 폰트의 데이터를 파싱하는 함수임
	public void extractData(File ufoFile){

		//ufoFile = new File(fileName);
		if(ufoFile.exists()){
			try{
				FileReader fileReader = new FileReader(ufoFile);
				BufferedReader br = new BufferedReader(fileReader);
				String line = null;
				int i = 0;  // 줄 수를 샌다.
				while((line = br.readLine()) != null){ //파일의 끝까지 읽음
					if(line.contains("<contour>")){ // 도형 하나 시작

						xList = new ArrayList<Integer>();  // 파싱된 x좌표 저장
						yList = new ArrayList<Integer>();   // 파싱된 y좌표 저장
					}

					if(line.contains("</contour>")){ // 도형 끝!
						letter = new Letter(xList, yList);
						letterList.add(letter);
						//xList, yList를 재활용 해서 다음 Letter를 생성할 것이기  때문에 null로 초기화
						xList = null;
						yList = null;
					}
					if (line.contains("type=\"line\"") || line.contains("type=\"curve\"") || line.contains("type=\"qcurve\"")){ // type이 있는 포인트만 추출

						String tokenline = line.substring(line.indexOf("x=\"")+3, line.length());;
						String intStr; // 잘려져 나온 숫자가 들어갈 변수임
						tokenizer = new StringTokenizer(tokenline, "\"");
						if(tokenizer.hasMoreTokens()){
							intStr = tokenizer.nextToken();

							int x = Integer.parseInt(intStr); //x를 저장
							xList.add(x);

							tokenline = line.substring(line.indexOf("y=\"")+3, line.length());
							tokenizer = new StringTokenizer(tokenline, "\"");
							intStr = tokenizer.nextToken();
							int y = reverseY(Integer.parseInt(intStr));
							yList.add(y);

							lineNumList.add(i); //line 넘버를 저장
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

	// 7/31 준은 추가 : GUI에서 파일을 받아와서 불러온 파일이 존재하는 폴더에 그대로 덮어쓴다.
	public void writeMetaUfo(ArrayList<Pair> pairList, File file){
		metaUfoStr = "";
		xList = new ArrayList<Integer>();  // 파싱된 x좌표 저장
		yList = new ArrayList<Integer>();   // 파싱된 y좌표 저장
		for(Letter l: letterList){
			for(int i=0;i<l.xArray.length;i++){
				xList.add(l.xArray[i]);
				yList.add(l.yArray[i]);
			}
		}
		try{
			//lineNumList에 저장된 줄을 가져와서 pair를 확인한 후 내용을 추가해준다
			if(ufoFile.exists()){
				FileReader fileReader = new FileReader(ufoFile);
				BufferedReader br = new BufferedReader(fileReader);
				String line = null;
				int lineNum = 0; // 현재 처리중인 line num

				while((line = br.readLine()) != null){
					if(lineNumList.size() > 1){ // 파싱한 데이터가 남아있으면 처리
						if(lineNumList.get(1) == lineNum){ // 수정할 라인일때 pair 정보를 찾아서 써준다.

							//해당 줄의 데이터가 페어링이 되었는지 찾는다.
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
									//pairList를 다 돌았는데도 안나오면 페어링에 실패한 점이다.
									//metaUfoStr = metaUfoStr + line + "\n";
									lineNumList.remove(1);
									xList.remove(0);
									yList.remove(0);
								}
							}
						}
					}
					//	if(lineNumList.get(0) != lineNum){ // 수정할 라인이 아닐때는 그대로 출력한다.
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

			checkDirection(file); // 페어링을 모두 잡은 후에 페어링 방향을 정해줌
			System.out.println(++cnt + ": " + fileName + " pairing complete");

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	//페어링의 방향을 결정하는 함수
	void checkDirection(File file) {
		/*
		 페어링이 모두 진행된 상태에서 pairing방향을 결정한다.
		 방법
		 파일의 처음부터 끝까지 차례대로 읽는다
		 처음에 읽힌 pairing 방향을 R로 지정
		 다음에 나오는 pairing 정보부터  pairing number가 바뀌면 같은 방향
		 연속으로 같은 pairing  number가 나오면 반대 방향으로 바꿔준다.
		*/

		int curPairingNum = 0;
		int prevPairingNum = 0;
		boolean direction = false; //false 면 L 아니면 R

		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader br = new BufferedReader(fileReader);

			BufferedWriter writer = new BufferedWriter(new FileWriter(file.getName()));
			String line = null;
			int lineNum = 0; // 현재 처리중인 line num
			int firstP, lastP; //pairing number의 처음위치와 마지막 위치
			StringBuilder stringBuilder;



			while ((line = br.readLine()) != null) {
				if (line.contains("penPair")) {
					stringBuilder = new StringBuilder(line);
					firstP = line.indexOf("penPair") + 10;
					lastP = line.indexOf("l\"",firstP);
					if(lastP == -1)
						lastP = line.indexOf("r\"",firstP);
					curPairingNum = Integer.parseInt(line.substring(firstP,lastP));

					if(curPairingNum == prevPairingNum)  // 이전 번호과 같은 경우 방향을 바꾼다.
						direction = !direction;

					if(direction)
						stringBuilder.setCharAt(lastP,'r');
					else
						stringBuilder.setCharAt(lastP,'l');

					prevPairingNum = curPairingNum;
					line = stringBuilder.toString();

				}
				writer.write(line);
				lineNum++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public File getUfoFile() {
		return this.ufoFile;
	}


}
