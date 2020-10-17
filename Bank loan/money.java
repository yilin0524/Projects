import java.io.*;
import java.util.*;
public class money {
	//static ArrayList meg = new ArrayList();
	
	public static void main(String[] args) {
		int blineNo=0,clineNo=0;
		//money.meg.add("");
		
        try {
        	LineNumberReader lnr = new LineNumberReader(new FileReader("banks.txt"));
			lnr.skip(Long.MAX_VALUE);
			blineNo = lnr.getLineNumber() + 1;
			lnr.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        try {
        	LineNumberReader lnr = new LineNumberReader(new FileReader("customers.txt"));
			lnr.skip(Long.MAX_VALUE);
			clineNo = lnr.getLineNumber() + 1;
			lnr.close();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        
        String customername[]=new String[clineNo];
        int customeraccount[]=new int[clineNo];
        String bankname[]=new String[blineNo];
        int bankaccount[]=new int[blineNo];
        HashMap<String, Integer> banks=new HashMap<String, Integer>();
        
		try {
            BufferedReader bankinput = new BufferedReader(new FileReader("banks.txt"));
            String line,bname;
            int bamount,index=0;
            
            while ((line = bankinput.readLine()) != null) {
            	int left=line.indexOf("{");
            	int mid=line.indexOf(",");
            	int right=line.indexOf("}");
            	bname=line.substring(left+1, mid);
            	bamount=Integer.parseInt(line.substring(mid+1, right));
            	
            	banks.put(bname, bamount);
            	
            	bankname[index]=bname;
            	bankaccount[index]=bamount;
            	index++;
            }
            bankinput.close();
        } catch (IOException e3) {
        	e3.printStackTrace();
        }
        
		try {
            BufferedReader customerinput = new BufferedReader(new FileReader("customers.txt"));
            String line,cname;
            int camount,index=0;
            
            while ((line = customerinput.readLine()) != null) {
            	int left=line.indexOf("{");
            	int mid=line.indexOf(",");
            	int right=line.indexOf("}");
            	cname=line.substring(left+1, mid);
            	camount=Integer.parseInt(line.substring(mid+1, right));

            	customername[index]=cname;
            	customeraccount[index]=camount;
            	index++;
            }      
            customerinput.close();
        } catch (IOException e4) {
        	e4.printStackTrace();
        }
		
		if(customername[customername.length-1]==null) {
			customername= Arrays.copyOf(customername,customername.length-1);
			customeraccount= Arrays.copyOf(customeraccount,customeraccount.length-1);
		}
		if(bankname[bankname.length-1]==null) {
			bankname= Arrays.copyOf(bankname, bankname.length-1);
			bankaccount= Arrays.copyOf(bankaccount, bankaccount.length-1);
		}
		
		System.out.println("** Customers and loan objectives **");
        for(int i=0;i<customername.length;i++) {
        	System.out.println(customername[i]+": "+customeraccount[i]);
        }
        System.out.print("\n");
        System.out.println("** Banks and financial resources **");
        for(int i=0;i<banks.size();i++) {
        	System.out.println(bankname[i]+": "+banks.get(bankname[i]));
        }
        System.out.print("\n");
        
        
        bankdata data=new bankdata(banks,customername.length);
        
        for(int i=0;i<bankname.length;i++) {	//bank threads
        		bank b=new bank(bankname[i],data);
        		Thread bthread= new Thread(b);
        		bthread.start();
        }
        for(int i=0;i<customername.length;i++) {	//customer threads
        		customer c=new customer(customername[i],customeraccount[i],bankname,data);
        		Thread cthread = new Thread(c);
        		cthread.start();
        }
        
        /*
        try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        while(true) {
        	try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	if(!money.meg.isEmpty()) {	//get the message
        		System.out.println(money.meg.get(0));
        		money.meg.remove(0);
        	}
        	else {
        		break;
        	}
        }
        */
        
	}
	
	
}

