package defo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// class for genetic operator
public class GeneticOP {

	double[][] Indivisuals;
	int [][] Belongings;
	double[] Fitness;

	public double[][] Shaping(double X[][][]) {
		int np=X.length;
		int c=X[0].length;
		int d=X[0][0].length;
		double Y [][] = new double [np][c*d];	// shaped matrix

		for(int i=0; i<np; i++) {
			int count=0;
			for(int j=0; j<c; j++) {
				for(int k=0; k<d; k++) {
					Y[i][count] = X[i][j][k];
					count++;
				}
			}
		}
		return Y;
	}

	public double[][][] DeShaping(double X[][], int np, int c, int d) {
		double Y [][][] = new double [np][c][d];	// shaped matrix

		for(int i=0; i<np; i++) {
			int count=0;
			for(int j=0; j<c; j++) {
				for(int k=0; k<d; k++) {
					Y[i][j][k] = X[i][count];
					count++;
				}
			}
		}
		return Y;
	}

	public double[] Evaluation(int U[][], double X[][], double ctr[][], int np, int c, int d, String dist){
		double f[] = new double [np];
		GeneticOP gop = new GeneticOP();
		double ctr3D [][][] = gop.DeShaping(ctr,np,c,d);
		KMop kmop = new KMop();

		for(int i=0; i<np; i++) {
			f[i] = kmop.CalcObjFunc(U[i], X, ctr3D[i], c, dist);
		}

		return f;
	}

	public double [][] Crossover(double P[][], double cp, String mtd, int rseed){
		int npop=P.length;
		int len=P[0].length;
		int no=(int)(npop*cp);
		double O[][] = new double [no][len];
		Sfmt rnd = new Sfmt(rseed);
		int Pcandind [] =  UniqueRndSelect(npop, no);

		switch(mtd){
			// Uniform crossover
			case "Uniform":
				for(int i=0; i<no; i=i+2) {
					double P1 [] = P[Pcandind[i]];		// selected parents
					double P2 [] = P[Pcandind[i+1]];
					double O1 [] = new double [len];	// offsprings
					double O2 [] = new double [len];
					for(int j=0; j<len; j++){
						if(rnd.NextUnif()>=0.5) {
							O1[j] = P1[j];
							O2[j] = P2[j];
						}else {
							O1[j] = P2[j];
							O2[j] = P1[j];
						}
					}

					O[i]=O1;
					O[i+1]=O2;
				}
				break;

			// Single point crossover
			case "Single":
				for(int i=0; i<no; i=i+2) {
					double P1 [] = P[Pcandind[i]];		// selected parents
					double P2 [] = P[Pcandind[i+1]];
					double O1 [] = new double [len];	// offsprings
					double O2 [] = new double [len];
					int cut = rnd.NextInt(len);
					for(int j=0; j<len; j++){
						if(j<cut) {
							O1[j] = P1[j];
							O2[j] = P2[j];
						}else {
							O1[j] = P2[j];
							O2[j] = P1[j];
						}
					}

					O[i]=O1;
					O[i+1]=O2;
				}
				break;

			case "Double":
				for(int i=0; i<no; i=i+2) {
					double P1 [] = P[Pcandind[i]];		// selected parents
					double P2 [] = P[Pcandind[i+1]];
					double O1 [] = new double [len];	// offsprings
					double O2 [] = new double [len];
					int cut [] = UniqueRndSelect(len, 2);
					Arrays.sort(cut);
					for(int j=0; j<len; j++){
						if(j<cut[0]) {
							O1[j] = P1[j];
							O2[j] = P2[j];
						}else if(j>=cut[0] && j<cut[1]){
							O1[j] = P2[j];
							O2[j] = P1[j];
						}else {
							O1[j] = P1[j];
							O2[j] = P2[j];
						}
					}

					O[i]=O1;
					O[i+1]=O2;
				}
				break;
		}

		return O;
	}

	public double [][] Mutation(double P[][], double mp, int rseed){
		int npop=P.length;
		int len=P[0].length;
		int nm=(int)(npop*mp);
		double O [][] = new double[nm][len];
		Sfmt rnd = new Sfmt(rseed);
		int Pcandind [] =  UniqueRndSelect(npop, nm);

		int count=0;
		for(int i=0; i<npop; i++) {
			if(FindVec(Pcandind,i)) {
				int j = rnd.NextInt(len);	// randomly chosen locus
				O[count] = P[i];
				if(P[i][j]!=(double)0) {
					if(rnd.NextUnif()>=0.5) {	// positive
						O[count][j] = P[i][j] + (2*rnd.NextUnif()*P[i][j]);
					}else {	// negative
						O[count][j] = P[i][j] - (2*rnd.NextUnif()*P[i][j]);
					}
				}else {
					if(rnd.NextUnif()>=0.5) {	// positive
						O[count][j] = P[i][j] + (2*rnd.NextUnif());
					}else {	// negative
						O[count][j] = P[i][j] - (2*rnd.NextUnif());
					}
				}
				count++;
			}
		}
		return O;
	}

	public GeneticOP Selection(double P[][], double f[], int U[][], int no, int ne, String mtd, int rseed){
		int np=P.length;
		int len=P[0].length;
		int len1=U[0].length;
		double O[][] = new double [no][len];
		int UO[][] = new int [no][len1];
		double fO[] = new double [no];
		int Pind [] = IndexSort(f);

		double P1 [][] = SortMatByInd(P, Pind);		// the parents matrix after sorted
		int U1 [][] = SortMatByIndInt(U, Pind);		// the parents matrix after sorted
		Arrays.sort(f);								// P1:f
		GeneticOP calc = new GeneticOP();			// the calculation result class
		Sfmt rnd = new Sfmt(rseed);

		switch(mtd) {
		case "Roulette":
			for(int i=0; i<ne; i++) {
			//elite preservation
					O[i] = P1[i];
					UO[i] = U[i];
					fO[i] = f[i];
			}

			double P2[][]= Arrays.copyOfRange(P1, ne, np);	// extract only not elite indivisuals
			int U2[][]= Arrays.copyOfRange(U1, ne, np);	// extract only not elite indivisuals
			double f2[]= Arrays.copyOfRange(f, ne, np);	// extract only not elite indivisuals
			double M[] = Inverse(f2);
			double Mn[] = Normalization(M);

			for(int i=ne; i<no; i++) {
				double rnum = rnd.NextUnif();
				int count = 0;  // counter for the element
		        double dSum = 0;
		        for (double nAccessCount : Mn) {
		            dSum += nAccessCount;
		            if (dSum >= rnum) {
		                break;
		            }
		            count++;
		        }

		        if(count>=P2.length) {
		        	O[i] = P2[P2.length-1];
		        	UO[i] = U2[P2.length-1];
			        fO[i] = f2[P2.length-1];
		        }else {
		        	O[i] = P2[count];
		        	UO[i] = U2[count];
		        	fO[i] = f2[count];
		        }
			}

			break;

		case "All":
			for(int i=0; i<no; i++) {
			//elite preservation
					O[i] = P1[i];
					UO[i] = U[i];
					fO[i] = f[i];
			}
			break;
		}

		calc.Indivisuals=O;
		calc.Belongings=UO;
		calc.Fitness=fO;

		return calc;
	}

    private void ShowVecDouble(double X[]) {
    	for(int i=0; i<X.length; i++) {
			System.out.printf("%.3f\n",X[i]);
		}
    }

    private void ShowMatDouble(double X[][]) {
    	for(int i=0; i<X.length; i++) {
			for(int j=0; j<X[0].length; j++) {
				if(j==X[0].length-1) {
					System.out.printf("%.3f\n",X[i][j]);
				}else {
					System.out.printf("%.3f ",X[i][j]);
				}
			}
		}
    }

	// select m of elements from 0-n
	private static int [] UniqueRndSelect(int n, int m) {
		List<Integer> list = new ArrayList<Integer>();
		int vec [] = new int [m];
		for(int i=0; i<n; i++){
			list.add(i);
	    }

		Collections.shuffle(list);

		for(int i=0; i<m; i++) {
			vec[i] = list.get(i);
		}
		return vec;
	}

	private static boolean FindVec(int X[], int a) {
		int n=X.length;
		boolean b=false;
		for(int i=0; i<n; i++) {
			if(X[i]==a) {
				b = true;
				break;
			}
		}

		return b;
	}

	private static double [] Inverse(double X[]) {
		int n=X.length;
		double Y[] = new double[n];
		for(int i=0; i<n; i++) {
			Y[i] = 1/X[i];
		}

		return Y;
	}

	private static double[] Normalization(double X[]) {
		int n=X.length;
		double Y [] = new double [n];
		double sum=0;
		for(int i=0; i<n; i++) {
			sum = sum + X[i];
		}
		for(int i=0; i<n; i++) {
			Y[i] = X[i]/sum;
		}

		return Y;
	}

	static int[] IndexSort(double X[]) {
		double[] X1 = Arrays.copyOf(X, X.length);
		Arrays.sort(X1);
		int[] Y = new int[X.length];
		for (int i = 0; i < X.length; i++) {
		    int index = -1;
		    for (int j = 0; j < X.length; j++) {
		        if (X[i] == X1[j]) {
		            index = j;
		            break;
		        }
		    }
		    Y[i] = index;
		}
		return Y;
	}

	static double[][] SortMatByInd(double X[][], int a[]) {
		int n = X.length;
		int len = X[0].length;
		double[][] Y = new double[n][len];
		for (int i = 0; i < n; i++) {
		    Y[a[i]] = X[i];
		}
		return Y;
	}

	static int[][] SortMatByIndInt(int X[][], int a[]) {
		int n = X.length;
		int len = X[0].length;
		int [][] Y = new int[n][len];
		for (int i = 0; i < n; i++) {
		    Y[a[i]] = X[i];
		}
		return Y;
	}
}
