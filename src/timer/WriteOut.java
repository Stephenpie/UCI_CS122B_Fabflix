package timer;

import java.io.*;


public class WriteOut {
	public long TJstartTime;
	public long TSstartTime;
	public long TJendTime;
	public long TSendTime;
	FileOutputStream out;
	PrintStream p;
	
	public WriteOut() {
		try {
			out = new FileOutputStream("/home/ubuntu/log.txt",true);
			p = new PrintStream(out);
		} catch(Exception e){
			System.out.println("error");
		}
	}
	
	public void writeTofileSearch(){
		try {
			p.print(String.valueOf(this.TSendTime - this.TSstartTime) + "\n");
			p.close();
			out.close();
		} catch(Exception e){
			System.out.println("error");
		}
	}
	
	public void writeTofileJdbc(){
		p.print(String.valueOf(this.TJendTime - this.TJstartTime) + "   ");	
	}
}