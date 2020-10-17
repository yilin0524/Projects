-module(money).

%% API
-export([start/0]).

% main function
start()->
  Cfile=file:consult("customers.txt"),
  Bfile=file:consult("banks.txt"),

  CList=element(2,Cfile),
  CNum=length(CList),
  io:fwrite("* * Customers and loan objectives * *\n"),
  for(CNum,CList),

  BList=element(2,Bfile),
  BNum=length(BList),
  io:fwrite("* * Banks and financial resources * *\n"),
  for(BNum,BList),
  BP=startb(BNum,BList,[],CNum),  % start bank processes
  %io:fwrite("~w\n",[BP]),
  CP=startc(CNum,CList,[],BP),  % start customer processes
  %io:fwrite("~w\n",[CP]),
  get_feedback(). % get feedback and output


% start bank processes
startb(0,_,Acc,_)->
  Acc;
startb(B,List,Acc,Cnum) when B>0->
  Tuple=lists:nth(length(List)-B+1,List),
  Name=element(1,Tuple),
  BankPid=spawn(bank, ba, [self(),Tuple,Cnum]),
  BankPid ! {self(), {bank,Tuple}},
  startb(B-1,List,Acc++[{Name,BankPid}],Cnum).


% start customer processes
startc(0,_,Acc,_)->
  Acc;
startc(C,List,Acc,BPid) when C>0->
  Tuple=lists:nth(length(List)-C+1,List),
  Name=element(1,Tuple),
  CusPid=spawn(customer, cu, [self(),BPid]),
  CusPid ! {self(), {customer,Tuple}},
  startc(C-1,List,Acc++[{Name,CusPid}],BPid).


% output the initial data
for(0,_) ->
  io:fwrite("\n");
for(N,List) when N > 0 ->
  Tuple=lists:nth(length(List)-N+1,List),
  io:fwrite("~s: ~w\n",[element(1,Tuple),element(2,Tuple)]),
  for(N-1,List).


% get feedback and output
get_feedback() ->
  receive
    {Cus, Money, Bank,1} ->
      io:fwrite("~s requests a loan of ~w dollars from ~s.\n", [Cus,Money,Bank]),
      get_feedback();
    {Bank, Money, Cus, 2} ->
      io:fwrite("~s approves a loan of ~w dollars from ~s.\n", [Bank,Money,Cus]),
      get_feedback();
    {Bank, Money, Cus, 3} ->
      io:fwrite("~s denies a loan of ~w dollars from ~s.\n", [Bank,Money,Cus]),
      get_feedback();
    {Cus, Money, 4} ->
      io:fwrite("~s has reached the objective of ~w dollars. Woo Hoo!\n", [Cus,Money]),
      get_feedback();
    {Cus, Money, 5} ->
      io:fwrite("~s was only able to borrow ~w dollars. Boo Hoo!\n", [Cus,Money]),
      get_feedback();
    {Bank, Money, 6} ->
      io:fwrite("~s has ~w dollars remaining.\n", [Bank,Money]),
      get_feedback()
  after 2000 -> true
  end.