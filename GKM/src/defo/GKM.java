package defo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

///////////////////////////////////////////////////////////////////////////////////
public class GKM {
	public void GenetickMeans(){	// FCM algorithm method
		File file = new File("C:\\JavaIO\\Input\\S1.txt");	// The file name
//		File file = new File("C:\\JavaIO\\Input\\xor2.txt");	// The file name
		int d = 2;	// the number of dimension of the data
		int data1[][] = DataRead(file,d);	// Load the data
//		double data1[][] = DataReadDouble(file,d);	// Load the data
		System.out.println();

		///////////////////////////////
		// Paramter Set & Initiation //
		///////////////////////////////

		// general parameters
		int c=15;											// the number of clusters
		int n=data1.length;									// the number of elements
		int maxtry=1;										// the number of maximum trials
		int maxite=300;										// the number of maximum iteration
		int rseed=1;										// random seed
		String dist="Euclid";								// the distance metric type

		// genetic operator parameters
		int np=40;					// the population size
		double cp=0.9;				// cossover probability
		double mp=0.1;				// mutation probability
		int ne=5;					// the number of elite indivisuals
		int cpop=(int)(np*cp);		// the number of individuals with crossover
		int mpop=(int)(np*mp);		// the number of individuals with crossover
//		String slctmtd="Roulette";	// the selection method
		String slctmtd="All";	// the selection method
		String crossmtd="Double";	// the crossover method

		double F [][] = new double [maxite][maxtry];		// Objective function value
		int Uarc [][] = new int [maxtry][n];				// Archive for membership
		double Xctrarc [][][] = new double [maxtry][c][d];	// Archive for cluster center

		// data normalization
		double data[][] = new double[n][d];
//		data=NormStd.Normalization(data1);
		data=NormStd.Standardization(data1);
//		data=data1;

		// create instances
		Sfmt rnd = new Sfmt(rseed);
		KMop kmop = new KMop();
		GeneticOP gop = new GeneticOP();
		GeneticOP calc = new GeneticOP();
		//
		double dataT [][] = Transpose(data);
		double lim[][] = new double [d][2];
		for(int i=0; i<d; i++) {
			lim[i][0] = Max(dataT[i]);
			lim[i][1] = Min(dataT[i]);
		}

		///////////////////////
//******// GKM Main Loop //********************************************************
		///////////////////////
		for(int trial=0; trial<maxtry; trial++) {
			int U [][] = new int [np][n];		// the cluster belonging vector (np*n)
			int U1 [][] = new int [cpop][n];
			int U2 [][] = new int [mpop][n];
			double Xctr [][][] = new double [np][c][d];	// the coordinate of cluster center (np*c*d)) -> population
			double Xctr1 [][][] = new double [cpop][c][d];
			double Xctr2 [][][] = new double [mpop][c][d];
			double pop [][] = new double [np][c*d];
			double pop1 [][] = new double [cpop][c*d];	// population created by crossover
			double pop2 [][] = new double [mpop][c*d];	// population created by mutation
			double fit [] = new double [np];
			double fit1 [] = new double [cpop];
			double fit2 [] = new double [mpop];

			// initiation of centroid (population)
			for(int i=0; i<np; i++) {
				for(int j=0; j<c; j++) {
					for(int k=0; k<d; k++) {
						Xctr[i][j][k] = ((lim[k][0]-lim[k][1]) * rnd.NextUnif()) + lim[k][1];
					}
				}
			}

			for(int i=0; i<np; i++) {
				U[i] = kmop.MembershipUpdate(data,Xctr[i],dist);
			}

			//////////////
			// GKM loop //
			//////////////


			for(int ite=0; ite<maxite; ite++) {
				// local search by k-Means
				for(int i=0; i<np; i++) {
					Xctr[i] = kmop.CalcCtr(U[i],data,c);	// the cluster center update
					U[i] = kmop.MembershipUpdate(data,Xctr[i],dist);
				}

				pop = gop.Shaping(Xctr);
				fit = gop.Evaluation(U, data, pop, np, c, d, dist);
				//ShowVecDouble(fit);
				//System.out.println();

				// Crossover from pop
				pop1 = gop.Crossover(pop, cp, crossmtd, rseed);
				Xctr1 = gop.DeShaping(pop1, cpop, c, d);
				for(int i=0; i<cpop; i++) {
					U1[i] = kmop.MembershipUpdate(data, Xctr1[i], dist);
				}
				fit1 = gop.Evaluation(U1, data, pop1, cpop, c, d, dist);

				//ShowVecDouble(fit1);
				//System.out.println();

				// Mutation from pop
				pop2 = gop.Mutation(pop, mp, rseed);
				Xctr2 = gop.DeShaping(pop2, mpop, c, d);
				for(int i=0; i<mpop; i++) {
					U2[i] = kmop.MembershipUpdate(data, Xctr2[i], dist);
				}
				fit2 = gop.Evaluation(U2, data, pop2, mpop, c, d, dist);

				//ShowVecDouble(fit2);
				//System.out.println();

				// Concatenation of all vectors produced
				double POP[][] = ConcatMatVertical(ConcatMatVertical(pop,pop1),pop2);
				double FIT[] = ConcatVecVertical(ConcatVecVertical(fit,fit1),fit2);
				int UU[][] = ConcatMatVerticalInt(ConcatMatVerticalInt(U,U1),U2);
				for(int i=0; i<FIT.length; i++) {
					if(Double.isNaN(FIT[i])) {
						FIT[i] = 999999999;
					}
				}
				//ShowVecDouble(FIT);
				//System.out.println();


				//int Pind [] = gop.IndexSort(FIT);
				//double P1 [][] = gop.SortMatByInd(POP, Pind);		// the parents matrix after sorted
				//int UU1 [][] = gop.SortMatByIndInt(UU, Pind);		// the parents matrix after sorted
				//Arrays.sort(FIT);

				//ShowVecDouble(FIT);
				//System.out.println();

				//for(int i=0; i<np; i++) {
					//elite preservation
				//			pop[i] = P1[i];
				//			U[i] = UU1[i];
				//			fit[i] = FIT[i];
				//}

				calc = gop.Selection(POP, FIT, UU, np, ne, slctmtd, rseed);

				Xctr = gop.DeShaping(calc.Indivisuals, np, c, d);
				U = calc.Belongings;

				fit = gop.Evaluation(U, data, calc.Indivisuals, np, c, d, dist);

				F[ite][trial] = fit[0];	// The maximum objective function update
//				F[ite][trial] = calc.Fitness[0];	// The maximum objective function update
				System.out.printf("Iteration: \t");
				System.out.printf("%d - %d\t",trial+1,ite+1);
				System.out.printf("OF Value: \t");
				System.out.printf("%.3f\n",F[ite][trial]);
			}

			// Data preservation for the archives
			Uarc[trial]=U[0];
			Xctrarc[trial]=Xctr[0];

		}

//*********************************************************************************

		// Extraction of the best data


		double Flast [] = new double [maxtry];
		for(int i=0; i<maxtry; i++) {
			Flast[i] = F[maxite-1][i];
		}
		double Fmin=9999999;
		int Find=0;
		for(int i=0; i<maxtry; i++) {
			if(Flast[i] < Fmin) {
				Find=i;
				Fmin=Flast[i];
			}
		}

		// Data output
		String Ufile = "C:\\JavaIO\\Output\\GKM\\U.txt";
		DataWriteVecInt(Ufile,Uarc[Find]);
		String Xfile = "C:\\JavaIO\\Output\\GKM\\X.txt";
		DataWrite(Xfile,data);
		String Xctrfile = "C:\\JavaIO\\Output\\GKM\\Xctr.txt";
		DataWrite(Xctrfile,Xctrarc[Find]);

	}

///////////////////////////////////////////////////////////////////////////////////

		/////////////
//******// Methods //**************************************************************
		/////////////
	//public int[][] DataRead(int n){
	public static int[][] DataRead(final File file, int d) {
        List<ArrayList<Integer>> lists = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < d; i++) {
            lists.add(new ArrayList<Integer>());
        }
        BufferedReader br = null;
        try {
            // Read the file, save data to List<Integer>
            br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                // Add nth integer to lists[n]
                List<Integer> ints = parse_line(line,d);
                for (int i = 0; i < d; i++) {
                    (lists.get(i)).add(ints.get(i));
                }
            }

            // convert lists to 2 Integer[]
            Integer[] array1 = lists.get(0).toArray(new Integer[lists.size()]);
            int n=array1.length;
            int data[][] = new int[n][d];

            int j=0;
            while(j<d) {
            	// convert lists to 2 Integer[]
                Integer[] array = lists.get(j).toArray(new Integer[lists.size()]);
	            for(int i=0; i<n; i++) {
	                	data[i][j]=array[i];
	            }

	            j++;
            }

            return data;

        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception ex) {
                // ignore error
            }
        }
        return null;
    }

    // parse 2 integers as a line of String
    private static List<Integer> parse_line(String line, int d) throws Exception {
        List<Integer> ans = new ArrayList<Integer>();
        StringTokenizer st = new StringTokenizer(line, " ");
        if (st.countTokens() != d) {
            throw new Exception("Bad line: [" + line + "]");
        }
        while (st.hasMoreElements()) {
            String s = st.nextToken();
            try {
                ans.add(Integer.parseInt(s));
            } catch (Exception ex) {
                throw new Exception("Bad Integer in " + "[" + line + "]. " + ex.getMessage());
            }
        }
        return ans;
    }

    public static double[][] DataReadDouble(final File file, int d) {
        List<ArrayList<Double>> lists = new ArrayList<ArrayList<Double>>();
        for (int i = 0; i < d; i++) {
            lists.add(new ArrayList<Double>());
        }
        BufferedReader br = null;
        try {
            // Read the file, save data to List<Integer>
            br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                // Add nth integer to lists[n]
                List<Double> ints = parse_line_double(line,d);
                for (int i = 0; i < d; i++) {
                    (lists.get(i)).add(ints.get(i));
                }
            }

            // convert lists to 2 Integer[]
            Double[] array1 = lists.get(0).toArray(new Double[lists.size()]);
            int n=array1.length;
            double data[][] = new double[n][d];

            int j=0;
            while(j<d) {
            	// convert lists to 2 Integer[]
                Double[] array = lists.get(j).toArray(new Double[lists.size()]);
	            for(int i=0; i<n; i++) {
	                	data[i][j]=array[i];
	            }

	            j++;
            }

            return data;

        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception ex) {
                // ignore error
            }
        }
        return null;
    }

    // parse 2 integers as a line of String
    private static List<Double> parse_line_double(String line, int d) throws Exception {
        List<Double> ans = new ArrayList<Double>();
        StringTokenizer st = new StringTokenizer(line, " ");
        if (st.countTokens() != d) {
            throw new Exception("Bad line: [" + line + "]");
        }
        while (st.hasMoreElements()) {
            String s = st.nextToken();
            try {
                ans.add(Double.parseDouble(s));
            } catch (Exception ex) {
                throw new Exception("Bad Integer in " + "[" + line + "]. " + ex.getMessage());
            }
        }
        return ans;
    }

    private static void DataWrite(String file_name, double data[][]) {
    	int n = data.length;	// the number of rows
    	int m = data[0].length;	// the number of colmuns

    	try {
            PrintWriter pw = new PrintWriter(file_name);
            for(int i=0; i<n; i++) {
            	for(int j=0; j<m; j++) {
            		if(j<m-1) {
                		pw.format("%.3f\t", data[i][j]);
            		}else {
                		pw.format("%.3f\n", data[i][j]);
            		}
                }
            }

            pw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void DataWriteVec(String file_name, double data[]) {
    	int n = data.length;	// the number of rows

    	try {
            PrintWriter pw = new PrintWriter(file_name);
            for(int i=0; i<n; i++) {
        		pw.format("%.3f\n", data[i]);
            }
            pw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void DataWriteVecInt(String file_name, int data[]) {
    	int n = data.length;	// the number of rows

    	try {
            PrintWriter pw = new PrintWriter(file_name);
            for(int i=0; i<n; i++) {
        		pw.format("%d\n", data[i]);
            }
            pw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
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

    private void ShowVecDouble(double X[]) {
    	for(int i=0; i<X.length; i++) {
			System.out.printf("%.3f\n",X[i]);
		}
    }

    private void ShowVecInt(int X[]) {
    	for(int i=0; i<X.length; i++) {
			System.out.printf("%d\n",X[i]);
		}
    }

    private static double Max(double X[]) {
    	int n=X.length;
    	double max = -99999999;
    	for(int i=0; i<n; i++) {
    		if(X[i]>max) {
    			max = X[i];
    		}
    	}
    	return max;
    }

    private static double Min(double X[]) {
    	int n=X.length;
    	double min = 99999999;
    	for(int i=0; i<n; i++) {
    		if(X[i]<min) {
    			min = X[i];
    		}
    	}
    	return min;
    }

    private static double[][] Transpose(double X[][]) {
    	int n=X.length;
    	int m=X[0].length;
    	double Y[][] = new double [m][n];
    	for(int i=0; i<m; i++) {
        	for(int j=0; j<n; j++) {
        		Y[i][j] = X[j][i];
        	}
    	}
    	return Y;
    }

    public static double[][] ConcatMatVertical(double X[][], double Y[][]) {
    	int nx=X.length;
    	int ny=Y.length;
    	int n=nx+ny;
    	int len=X[0].length;
    	double Z [][] = new double [n][len];

    	for(int i=0; i<nx; i++) {
    		Z[i] = X[i];
    	}
    	int j=nx;
    	for(int i=0; i<ny; i++) {
    		Z[j] = Y[i];
    		j++;
    	}

    	return Z;
    }

    public static int[][] ConcatMatVerticalInt(int X[][], int Y[][]) {
    	int nx=X.length;
    	int ny=Y.length;
    	int n=nx+ny;
    	int len=X[0].length;
    	int Z [][] = new int [n][len];

    	for(int i=0; i<nx; i++) {
    		Z[i] = X[i];
    	}
    	int j=nx;
    	for(int i=0; i<ny; i++) {
    		Z[j] = Y[i];
    		j++;
    	}

    	return Z;
    }

    public static double[] ConcatVecVertical(double X[], double Y[]) {
    	int nx=X.length;
    	int ny=Y.length;
    	int n=nx+ny;
    	double Z [] = new double [n];

    	for(int i=0; i<nx; i++) {
    		Z[i] = X[i];
    	}
    	int j=nx;
    	for(int i=0; i<ny; i++) {
    		Z[j] = Y[i];
    		j++;
    	}

    	return Z;
    }



//*********************************************************************************


}
