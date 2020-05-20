package defo;

import java.util.ArrayList;
import java.util.List;

public class KMop {
    // cluster centroid calculation method
    public double [][] CalcCtr(int U[], double X[][], int c){
    	int n = X.length;		// the number of elements
       	int d = X[0].length;	// the number of dementions
    	double ctr [][] = new double [c][d];	// the cluster center
    	StatCalcMat mat = new StatCalcMat();

    	for(int i=0; i<d; i++) {	    // loop for dimension
    		for(int j=0; j<c; j++) {	// loop for cluster
        		List<Double> list = new ArrayList<Double>();
        		for(int k=0; k<n; k++) {	// loop for element
        			if(U[k]==j) {
        				list.add(X[k][i]);

        			}
        		}
        		Double [] array = list.toArray(new Double[list.size()]);
        		double [] meanvec = new double [array.length];
        		for(int k=0; k<array.length; k++) {
        			meanvec[k] = array[k];
        		}
        		ctr[j][i] = mat.MeanVecDouble(meanvec);
    		}
    	}
    	return ctr;
    }

    public int [] MembershipUpdate(double X[][], double ctr[][], String dist) {
    	int n = X.length;		// the number of elements
    	int c = ctr.length;		// the cluster center
    	int [] U = new int [n];	// new cluster membership
    	double a;

    	for(int i=0; i<n; i++) {
    		double amin = 99999999;
    		int ind = 0;
        	for(int j=0; j<c; j++) {
        		a = Dist2Points(X[i], ctr[j], dist);
        		if(a < amin) {
        			amin = a;
        			ind = j;
        		}
        	}
        	U[i] = ind;
    	}

    	return U;
    }

    public double CalcObjFunc(int U[], double X[][], double ctr[][], int nc, String dist) {
    	double F=0;
		int n=X.length;			// the number of elements

		for(int k = 0; k<nc; k++) {
			for(int i = 0; i<n; i++) {
				if(U[i]==k) {
					if(Double.isNaN(ctr[k][0])==false) {
						F = F + Dist2Points(X[i],ctr[k],dist);
					}else {
						F = 999999999;
					}
				}
			}
		}


    	return F;
    }

    private static double Dist2Points(double X[], double Y[], String dist) {
    	int d = X.length;
    	double D = 0;
//		int count=1;
		double xsum=0;

		switch (dist) {
		case "Euclid":	// Metric: Euclidean distance

			for(int i=0; i<d; i++) {
				xsum = xsum + Math.pow((X[i]-Y[i]),2);
			}
			D=Math.sqrt(xsum);

			break;
			/*
		case "SEuclid":	// Metric: Scaled Euclidean distance
			double[] X = new double[nd];
			double[] sigma = new double[nf];
			double val;

			StatCalc stat = new StatCalc();
			for(int i=0; i<nf; i++) {
				X = getCul(data,i);
				sigma[i] = stat.Var(X);
			}

			for(int i=0; i<nd; i++) {
				for(int j=count; j<nd; j++) {
					for(int k=0; k<nf; k++) {
						val = ((Math.pow((data[i][k]-data[j][k]),2)) / sigma[k]);
						if(Double.isNaN(val) == false) {
							xsum = xsum + val;
						}
					}
					D[i][j]=Math.sqrt(xsum);
					xsum = 0;
				}
				count = count+1;
			}
			D = Dissim.matUcopy2L(D);

			break;
		case "City":	// Metric: city block distance

			for(int i=0; i<nd; i++) {
				for(int j=count; j<nd; j++) {
					for(int k=0; k<nf; k++) {
						xsum = xsum + Math.abs(data[i][k]-data[j][k]);
					}
					D[i][j] = xsum;
					xsum = 0;
				}
				count = count+1;
			}
			D = Dissim.matUcopy2L(D);

			break;
		case "Chebyshev":	// Metric: Chebyshev distance
			double dmax = 0;
			double a = 0;

			for(int i=0; i<nd; i++) {
				for(int j=count; j<nd; j++) {
					for(int k=0; k<nf; k++) {
						a = Math.abs(data[i][k]-data[j][k]);
						if(dmax < a) {
							dmax = a;
						}
					}
					D[i][j] = dmax;
					dmax=0;
				}
				count=count+1;
			}
			D = Dissim.matUcopy2L(D);

			break;
		case "Mahalanobis":
			break;
		case "Cosine":
			break;
			*/
		}
		return D;
	}
}
