package GA;
import java.util.Random;

public class a_linear_equation {

	public a_linear_equation() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	//誤差二乗の計算
	public static double error(double x){
		return Math.pow(3*x-6,2);
	}

	//突然変種の設定
	public static String set_mutant(String species){
		Random random = new Random();
		int digit = random.nextInt(3);
		String s = null;
		switch (digit){
		case 0:
			if(species.substring(digit,digit+1).equals("1")){
				s ="0"+species.substring(1);
			}else{
				s ="1"+species.substring(1);
			}
			break;
		case 1:
			if(species.substring(digit,digit+1).equals("1")){
				s =species.substring(0,1)+"0"+species.substring(2);
			}else{
				s =species.substring(0,1)+"1"+species.substring(2);
			}
			break;
		case 2:
			if(species.substring(digit).equals("1")){
				s =species.substring(0,2)+"0";
			}else{
				s =species.substring(0,2)+"1";
			}
			break;
		}
		return s;
	}


	//すでに同じものが存在しているかのチェック
	public static int check_existance(String checker,int n, String[] species){
		int num=-1;
		for(int i=0;i<n;i++){
			if(species[i]!=null){
				if(species[i].equals(checker)){
					num = i;
				}
			}
		}
		return num;
	}

	//初期設定。設定を変えたいときはここをいじる。
	//設定されてないものは-1としておく。
	public static void initialize(
		String[] species,
		long[] number,
		double[] error,
		double[] fittness,
		double[] fittness_times_number,
		int n
	){
		species[0] = "011";
		species[1] = "101";
		species[2] = "111";
		number[0] = 33;
		number[1] = 33;
		number[2] = 34;
		for(int i = 0;i<n;i++){
			if(species[i]==null){
				number[i] = -1;
				error[i] = -1;
				fittness[i] = -1;
				fittness_times_number[i] = -1;
			}
		}
	}

	public static void main(String[] args) {
		//How many species do I expect?
		int n = 97;
		//How many times do the step repeat?
		int m = (n - 3)/2;

		//各値を格納する配列
		//number配列に、各要素の個体数、species配列には個体の名前を、それぞれの番号で紐づいたものとして、記録していく。
		//speciesがnull→種が未誕生
		//numberが0→種が存在はしていたが、滅びた。
		String[] species = new String[n];
		long[] number = new long[n];
		double[] error = new double[n];
		double[] fittness = new double[n];
		double[] fittness_times_number = new double[n];

		//初期設定
		initialize(species,number,error, fittness,fittness_times_number,n);

		//出力用記述
		System.out.println("\t種\t個体数");

		int times = 0;
		loop:while(times<m){

			/*
			 *交叉と突然変異に関して、新ルール適用
			 *親世代の種の個体の多い順に上から2種が、交叉する
			 *突然変異種は親世代に存在していた種のうち一番新しいものの遺伝子のうちから、、
			 *ランダムに選んだ一つをひっくり返す。
			 */
			String mutant = null;
			String child  = null;
			int max_index = 0;
			int next_index = 1;
			int last_species = 0;
			for(int i=0;i<n;i++){
				if(species[i]!=null&&number[i]>0){
					last_species = i;
					if(number[i]>number[max_index]){
						max_index=i;
					}else{
						if(number[i]>=number[next_index]&&i!=max_index){
							next_index = i;
						}
					}
				}
			}
			child = species[max_index].substring(0, 2)+species[next_index].substring(2);
			mutant = set_mutant(species[last_species]);



			//誤差の二乗の計算と最大値の算出
			double max = error(Integer.parseInt(species[0],2));
			for(int i = 0;i<n;i++){
				if(number[i]>0){
					error[i] = error(Integer.parseInt(species[i],2));
					if(error[i]==0){
						System.out.println("最適種は、"+species[i]+"です。");
						break loop;
					}
					if(max<=error[i]){
						max = error[i];
					}
				}
			}

			//適合度の計算と合計の算出
			double sum=0;
			for(int i = 0;i<n;i++){
				if(number[i]>0){
					fittness[i] = max - error[i];
					fittness_times_number[i]=fittness[i]*number[i];
					sum+=fittness_times_number[i];
				}
			}

			//ルーレット法と次世代の個数決定
			for(int i = 0;i<n;i++){
				if(number[i]>0){
					number[i] = Math.round(98 * fittness_times_number[i]/sum);
				}
			}

			//交叉と突然変異
			//最初の種が突然変異する(過去に生じたものへは、遺伝的に記憶されているとして、突然変異しないものとする)
			//最初の種と二番目の種が交配する。二個目の前2個と1個目の後ろ1個が交配する。
//			boolean step1 = false;
//			boolean step2 = false;
			boolean step3 = false;
			boolean step4 = false;
//			String mutant = null;
//			String child  = null;
//			for(int i = 0;i<n;i++){
//				if(number[i]>0){
//					if(step2 != true){
//						if(step1==true){
//							step2 = true;
//							child = species[i].substring(0, 2)+species[i-1].substring(2);
//							//System.out.println(child);
//						}else{
//							step1 = true;
//							mutant = set_mutant(species[i]);
//							//System.out.println(mutant);
//						}
//					}
//				}
//			}

			//次世代種の作成
			int mutant_existance =check_existance(mutant,n,species);
			int child_existance =check_existance(child,n,species);
			if(mutant_existance>=0){number[mutant_existance] += 1;
			}else{
				if(child_existance>=0){number[child_existance] += 1;
				}else{
					for(int i =0;i<n;i++){
						if(number[i]==-1&&species[i]==null){
							if(step4 != true){
								if(step3==true){
									step4 = true;
									species[i] = child;
									number[i] = 1;
								}else{
									step3 = true;
									species[i] = mutant;
									number[i] = 1;
								}
							}
						}
					}
				}
			}
			times++;

			System.out.println(times+"回目");
			//一回のstepが終了した段階で次世代の種とその個体数を表示させる
			for(int i=0;i<n;i++){
				if(number[i]>0){
					System.out.println("\t"+species[i]+"\t"+number[i]);
				}
			}
		}

		//最も誤差が小さいものを言う。
		//
		int optimum=0;
		for(int i = 0;i<n;i++){
			if(number[i]>0){
				if(error(Integer.parseInt(species[i],2))<error[optimum]&&Integer.parseInt(species[i],2)>=0){
					optimum = i;
				}
			}
		}
		System.out.println("最も誤差が小さかったのは"+species[optimum]+"です");
	}






}