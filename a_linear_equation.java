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


	/*
	 *交叉と突然変異に関して、新ルール適用
	 *親世代の種の個体の多い順に上から2種が、交叉する
	 *突然変異種は親世代に存在していた種のうち一番新しいものの遺伝子のうちから、、
	 *ランダムに選んだ一つをひっくり返す。
	 */

	//突然変種の設定
	public static String set_mutant(String species){
		Random random = new Random();
		int digit = random.nextInt(3);//何桁目を変異させるかを導出
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

	//交配の設定
	public static String set_child(String[] species , long[] number , int[] last_species, int n){
		int max_index = 0;
		int next_index = 1;
		for(int i=0;i<n;i++){
			if(species[i]!=null&&number[i]>0){
				last_species[0] = i;
				if(number[i]>number[max_index]){
					max_index=i;
				}else{
					if(number[i]>=number[next_index]&&i!=max_index){
						next_index = i;
					}
				}
			}
		}
		return species[max_index].substring(0, 2)+species[next_index].substring(2);
	}

	//誤差の二乗の計算と最大値の算出
	public static double set_max_error(String[] species , long[] number , double[] error,int[] last_species, int n){
		double max = error(Integer.parseInt(species[last_species[0]],2));
		for(int i = 0;i<n;i++){
			if(number[i]>0){
				error[i] = error(Integer.parseInt(species[i],2));
				if(error[i]==0){
					System.out.println("最適種は、"+species[i]+"です。");
				}
				if(max<=error[i]){
					max = error[i];
				}
			}
		}
		return max;
	}

	//適合度の計算と合計の算出
	public static double set_sum_fittness(long[] number,double[] fittness, double[] fittness_times_number,double[] error, double max,int n){
		double sum=0;
		for(int i = 0;i<n;i++){
			if(number[i]>0){
				fittness[i] = max - error[i];
				fittness_times_number[i]=fittness[i]*number[i];
				sum+=fittness_times_number[i];
			}
		}
		return sum;
	}

	//ルーレット法と次世代の個数決定(突然変異種交配種の個体数は含まない)
	public static void calculation_results(String[] species,long[] number, double[] fittness_times_number, double sum,int n){
		for(int i = 0;i<n;i++){
			if(number[i]>0){
				number[i] = Math.round(98 * fittness_times_number[i]/sum);
				System.out.println(species[i]+"\t"+number[i]);
			}
		}
	}

	//すでに同じものが存在しているかのチェック。
	//していたらその番号を、していなかったら-1を返す
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

	//突然変異種・交配種の設定
	public static void set_next_generation(String[] species, long[] number,boolean step, String newer, int n){
		for(int i =0;i<n;i++){
			if(number[i]==-1&&species[i]==null){
				if(!step){
					step = true;
					species[i] = newer;
					number[i] = 1;
				}
			}
		}
	}
	//突然変異種・交配種を含めた次世代の種の個体数を決定
	public static void set_next_generations(String[] species, long[] number, String child,String mutant, int n){
		//次世代種の作成
		boolean[] step = {false, false};//何も登録されてないところに新たな子供と突然変異種を登録するための番号を記録するためのモノ
		int mutant_existance =check_existance(mutant,n,species);
		int child_existance =check_existance(child,n,species);
		if(mutant_existance>=0&&child_existance>=0){
			number[mutant_existance] += 1;
			number[child_existance] += 1;
		}else{
			if(mutant_existance>=0){
				number[mutant_existance] += 1;
				set_next_generation(species,number,step[0], child,n);
			}else{
				if(child_existance>=0){
					number[child_existance] += 1;
					set_next_generation(species,number,step[0], mutant,n);
				}else{
					for(int i =0;i<n;i++){
						if(number[i]==-1&&species[i]==null){
							if(!step[1]){
								if(step[0]){
									step[1] = true;
									species[i] = child;
									number[i] = 1;
								}else{
									step[0] = true;
									species[i] = mutant;
									number[i] = 1;
								}
							}
						}
					}
				}
			}
		}
	}

	//最適種の探索
	public static int set_optimum(String[] species, double[] error, long[] number, int n){
		int optimum = 0;
		for(int i = 0;i<n;i++){
			if(number[i]>0){
				if(error(Integer.parseInt(species[i],2))<error[optimum]&&Integer.parseInt(species[i],2)>=0){
					optimum = i;
				}
			}
		}
		return optimum;
	}

	//次世代種の個体数と種を表示
	public static void print_next_generations(String[] species, long[] number, int n,int times){
		System.out.println(times+"回目");
		for(int i=0;i<n;i++){
			if(number[i]>0){
				System.out.println("\t"+species[i]+"\t"+number[i]);
			}
		}
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
		int n = 97;//How many species do I expect?
		int m = (n - 3)/2;//How many times do the step repeat?

		/*
		 * 各値を格納する配列
		 * number配列に、各要素の個体数、species配列には個体の名前を、それぞれの番号で紐づいたものとして、記録していく。
		 * speciesがnull→種が未誕生
		 * numberが0→種が存在はしていたが、滅びた。
		 */
		String[] species = new String[n];//種を記録
		long[] number = new long[n];//個体数を記録
		double[] error = new double[n];//誤差の二乗を記録
		double[] fittness = new double[n];//適合度を記録
		double[] fittness_times_number = new double[n];//適合度×個体数を記録

		initialize(species,number,error, fittness,fittness_times_number,n);//初期設定
		System.out.println("\t種\t個体数");//出力用記述

		int times = 0;	//何回目の世代交代かを記述
		while(times<m){

			int[] last_species = {0};
			String child  = set_child(species,number,last_species,n);
			String mutant = set_mutant(species[last_species[0]]);

			//誤差の二乗の計算と最大値の算出
			double max = set_max_error(species,number,error,last_species,n);

			//適合度の計算と合計の算出
			double sum=set_sum_fittness(number,fittness,fittness_times_number,error, max,n);

			//ルーレット法と次世代の個数決定
			System.out.println("計算結果");
			calculation_results(species,number,fittness_times_number,sum,n);

			System.out.println("突然変異種："+mutant);
			System.out.println("交配結果："+child);

			set_next_generations(species,number,child,mutant,n);
			times++;
			print_next_generations(species,number,n,times);//一回のstepが終了した段階で次世代の種とその個体数を表示させる
		}

		System.out.println("最も誤差が小さかったのは"+species[set_optimum(species,error,number,n)]+"です");
	}






}