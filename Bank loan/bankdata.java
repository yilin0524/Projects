import java.util.HashMap;

class bankdata{
	public HashMap<String, Integer> bankmap=new HashMap<String, Integer>();
	public int flag=0;
	private int ccount;
	
	public bankdata(HashMap b,int cnum) {
		this.bankmap=b;
		this.ccount=cnum;
	}
	
	public synchronized String checkloan(String customername,String bankname, int loanamount) {	//check customer's request
		String message="";
		if((bankmap.get(bankname)-loanamount)>=0) {
			bankmap.put(bankname, bankmap.get(bankname)-loanamount);
			message=bankname+" approves a loan of "+loanamount+" dollars from "+customername+".";
		}else {
			message=bankname+" denies a loan of "+loanamount+" dollars from "+customername+".";
		}
		return message;
	}
	
	
	public String getavailable(String bankname) {	//get remaining of a bank
		String message=bankname+" has "+bankmap.get(bankname)+" dollars remaining.";
		return message;
	}
	
	public void showavailable(String finish){	//check all customer finished their loan
		if(finish!=null) {
			ccount--;
		}
		if(ccount==0) {
			flag=1;
		}
	}
	
}