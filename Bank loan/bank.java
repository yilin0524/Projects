class bank implements Runnable{
	private bankdata data;
	public String bankname;
	
	public bank(String name, bankdata d) {
		this.data=d;
		this.bankname=name;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//System.out.println(bankname +"  running!!!");
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(true) {
			try {
				Thread.sleep(40);
				if(data.flag==1) {
					/*
					synchronized(money.meg) {
						money.meg.add(data.getavailable(bankname));
					}
					*/
					System.out.println(data.getavailable(bankname));	//get remaining
					break;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
}