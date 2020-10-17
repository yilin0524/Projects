import java.util.Arrays;
import java.util.Random;

class customer implements Runnable{
	private bankdata data;
	private String customername;
	private int total;
	private int loan;
	private String banks[];
	
	public customer(String name, int l, String[] b, bankdata d) {
		this.customername=name;
		this.loan=l;
		this.total=l;
		this.banks=b;
		this.data=d;
	}
	
	public String loanrequest() {	//request the loans
		String result = null;
		String message;
		int moneyloan, banknum=banks.length;
		Random random = new Random();
		
		while(loan>0&&banknum>0) {
			int index=random.nextInt(banks.length);
			moneyloan=random.nextInt((50 - 1) + 1) + 1;
			if((loan-moneyloan)<0) {
				continue;
			}
			else {
				int millisecond=random.nextInt((100 - 10) + 1) + 10;
				try {
					Thread.sleep(millisecond);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				message=data.checkloan(customername,banks[index], moneyloan);
				/*
				synchronized(money.meg) {
					money.meg.add(customername+" requests a loan of "+moneyloan+ " dollars from "+banks[index]+".");
				}
				*/
				System.out.println(customername+" requests a loan of "+moneyloan+ " dollars from "+banks[index]);
				boolean status = message.contains("approves");
				if(status) { //the request be approved
					loan=loan-moneyloan;
				}else {	//the request be denied
					banknum--;
					if(index!=banks.length-1) {
						banks[index]=banks[banks.length-1];
						banks = Arrays.copyOf(banks, banks.length-1);
					}else {
						banks = Arrays.copyOf(banks, banks.length-1);
					}
					
				}
				/*
				synchronized(money.meg) {
					money.meg.add(message);
				}
				*/
				System.out.println(message);
			}
		}
		
		if(loan>0) {
			result=customername+" was only able to borrow "+(total-loan)+ " dollars. Boo Hoo!";
		}else if(loan==0) { 	//finish all money needed
			result=customername+" has reached the objective of "+ total+" dollars. Woo Hoo!";
		}
		
		return result;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//System.out.println(customername +"  running!!!");
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String finish=loanrequest();
		
		data.showavailable(finish);
		/*
		synchronized(money.meg) {
			money.meg.add(finish);
		}
		*/
		System.out.println(finish);
		
	}
}
