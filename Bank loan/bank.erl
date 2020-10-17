-module(bank).


%% API
-export([ba/3]).

ba(MPid,BTuple,Cnum)->
  receive
    finish ->   % customers finish their loan operation
      if
        Cnum-1>0 -> ba(MPid,BTuple,Cnum-1);
        Cnum-1==0 ->
          MPid ! {element(1,BTuple),element(2,BTuple),6}
      end;
    {Sender, {bank,Tuple}} ->   % new bank process
      timer:sleep(100),
      ba(MPid,Tuple,Cnum);
    {Sender, Cus, Money} ->   % customers' request
      Name=element(1,BTuple),
      Resource=element(2,BTuple),
      if
        Resource-Money>=0 ->
          Sender ! approve,   % approve the loan
          MPid ! {Name, Money, Cus, 2},
          ba(MPid,{Name, Resource-Money},Cnum);
        Resource-Money<0 ->
          Sender ! deney,   % reject the loan
          MPid ! {Name, Money, Cus, 3},
          ba(MPid, BTuple,Cnum)
      end

  end.


