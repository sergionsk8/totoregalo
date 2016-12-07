package sergionsk8.totonatale;

import java.util.List;
import java.util.ArrayList;

public class tototest {
	List<String> partecipanti;
	int[][] A;
	
	public tototest(List<String> partecipanti) {
		this.partecipanti = new ArrayList<String>(partecipanti);
		this.A = new int[partecipanti.size()][partecipanti.size()];
		
		int rows = A.length;
        int columns = A[0].length;
		for(int i=0;i<rows;i++){
            for(int j=0;j<columns;j++){
                A[i][j] = 0;
            }
        }
	}
	
	public void add(String partecipante, String ricevente) {
		int p = this.partecipanti.indexOf(partecipante);
		int r = this.partecipanti.indexOf(ricevente);
		this.A[p][r] += 1;
	}
	
	public void print() {
		int rows = A.length;
        int columns = A[0].length;
        String str = "|  ";

        for(int i=0;i<rows;i++){
        	String part = this.partecipanti.get(i).substring(0, 4);
            for(int j=0;j<columns;j++){
                str += A[i][j] + "\t";
            }

            System.out.println(part + str + "|");
            str = "|  ";
        }
        System.out.print("\t   ");
        for (String part : this.partecipanti)
        	System.out.print(part.substring(0, 4) + "\t");
	}
}