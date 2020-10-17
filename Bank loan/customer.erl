-module(customer).

%% API
-export([cu/2]).

cu(MPid,BP)->
  receive
    {Sender, {customer,Tuple}} ->   % new customer process
      timer:sleep(100),
      Name=element(1,Tuple),
      L=element(2,Tuple),
      request(MPid,Name,L,L, BP,BP)   % start the loan
  end.


% get a random number
number(Same, Same) -> Same;
number(Min,Max)->
  M = Min - 1,
  random:uniform(Max - M) + M.


% send the loan request
request(MPid, Name, Loan, Total, BP, AllB)->
  if
    Loan>0 andalso length(BP)>0->   % loan not finish
      random:seed(erlang:now()),
      MS=number(10,100),
      Money=number(1,50),
      Index=number(1,length(BP)),

      %io:fwrite("~s: index: ~w, money: ~w, sleep: ~w\n",[Name,Index,Money,MS]),
      if
        Loan-Money>=0 ->
          timer:sleep(MS),
          BPid=element(2,lists:nth(Index,BP)),
          BName=element(1,lists:nth(Index,BP)),
          BPid ! {self(),Name,Money}, % send request
          MPid ! {Name, Money, BName, 1},
          receive
            approve ->  % request was approved
              request(MPid, Name, Loan-Money,Total,BP, AllB);
            deney ->  % request was rejected
              request(MPid, Name, Loan, Total, lists:delete(lists:nth(Index,BP),BP),AllB)
          end;
        Loan-Money<0 ->
          request(MPid,Name,Loan,Total,BP,AllB)
      end;
    Loan==0 ->  % finish all loan
      for(length(AllB),AllB), % notice banks
      MPid ! {Name, Total, 4};
    Loan>0 andalso length(BP)==0-> % finish but not achieve the goal
      for(length(AllB),AllB),
      MPid ! {Name, Total-Loan, 5}
  end.


% notice all banks
for(0,_) ->
  [];
for(N,List) when N > 0 ->
  Pid=element(2,lists:nth(length(List)-N+1,List)),
  Pid ! finish,
  for(N-1,List).