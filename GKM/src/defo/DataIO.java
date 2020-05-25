package defo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class DataIO {
	//public int[][] DataRead(int n){
	public int[][] DataRead(final File file, int d) {
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

    public double[][] DataReadDouble(final File file, int d) {
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

    public void DataWrite(String file_name, double data[][]) {
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

    public void DataWrite(String file_name, int data[][]) {
    	int n = data.length;	// the number of rows
    	int m = data[0].length;	// the number of colmuns

    	try {
            PrintWriter pw = new PrintWriter(file_name);
            for(int i=0; i<n; i++) {
            	for(int j=0; j<m; j++) {
            		if(j<m-1) {
                		pw.format("%d\t", data[i][j]);
            		}else {
                		pw.format("%d\n", data[i][j]);
            		}
                }
            }

            pw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void DataWriteVec(String file_name, double data[]) {
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

    public void DataWriteVec(String file_name, int data[]) {
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

    public void DataWriteVecInt(String file_name, int data[]) {
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
}
