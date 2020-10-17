/**
* @author  Yilin Li 40083064
*/
#include <iostream>
#include <stdlib.h>
#include <time.h>
#include <set>
#include <array>
using namespace std;
#include "SlotMachine.h"
#include "Shape.h"
#include "Rectangle.h"
#include "Rhombus.h"
#include "AcuteTriangle.h"
#include "RightTriangle.h"

void SlotMachine::run(int t) {
	int currentToken = t;
	int bet;
	cout << "Welcome to 3-Reel Lucky Slot Machine Game!" << endl;
	cout << "Each reel will randomly display one of four shapes, each in 25 sizes." << endl;
	cout << "To win 3 x bet, get 2 similar shapes AND 2 shapes with equal Scr Areas" << endl;
	cout << "To win 2 x bet, get 3 similar shapes" << endl;
	cout << "To win 1 x bet, get(Middle) Scr Area > (Left + Right) Scr Areas" << endl;
	cout << "To win or lose nothing, get same Left and Right shapes" << endl;
	cout << "Otherwise, you lose your bet." << endl;
	cout << "You start with "<< t <<" free slot tokens!" << endl;
	while (true) {
		int type[3];//3 types
		set<int> area;// Scr area
		string info[3][3];// record 3 shapes' name, height, width

		if (currentToken <= 0) { //ran out of tokens
			cout << "You just ran out of tokens. Better luck next time!" << endl;
			cout << "Game Over." << endl;
			break;
		}	

		cout << endl;
		cout << "How much would you like to bet(enter 0 to quit) ? ";
		cin >> bet;
		if (bet<0) {//wrong input
			cout << "Invaild input! Try again!"<<endl;
			continue;
		}
		else if (cin.fail()) {//wrong input
			cout << "Invaild input!" << endl;
			throw(invalid_argument("Error: Invaild input!"));
			break;
		}
		else if (bet == 0) {//quit
			cout << "Thank you for playing, come back soon!" << endl;
			cout << "Be sure you cash in your remaining " << currentToken << " tokens at the bar!" << endl;
			cout << "Game Over." << endl;
			break;
		}else if(bet>currentToken){// invaild bet
			cout<<"You can¡¯t bet more than "<<currentToken<<", try again!"<<endl;
			continue;
		}
		else {
			srand((int)time(0));
			for (int k = 0; k < 3; ++k) {
				int n = rand() % 4;
				int w = rand() % 25 + 1;

				type[k] = n;//record type

				if (n == 0) {
					reel[k].reset(new Rhombus(w));
				}
				else if (n == 1) {
					reel[k].reset(new AcuteTriangle(w));
				}
				else if (n == 2) {
					reel[k].reset(new RightTriangle(w));
				}
				else {
					int h = rand() % 25 + 1;
					reel[k].reset(new Rectangle(w, h));
				}

				area.insert(reel[k]->screenArea());//record scr area
				
				info[k][0] = reel[k]->name_getter(); //record info
				info[k][1] = to_string(reel[k]->getHeight());
				info[k][2] = to_string(reel[k]->getWidth());
			}

			//shapes' draw
			Grid box_0 = reel[0]->draw();
			Grid box_1 = reel[1]->draw();
			Grid box_2 = reel[2]->draw();

			int heightSize;
			if (box_1.size() >= box_2.size() && box_1.size() >= box_0.size()) {
				heightSize = box_1.size();
			}
			else if (box_2.size() > box_1.size() && box_2.size() >= box_0.size()) {
				heightSize = box_2.size();
			}
			else if (box_0.size() > box_1.size() && box_0.size() > box_2.size()) {
				heightSize = box_0.size();
			}
			int hh = heightSize+4;
			int ww = 10 + box_0[0].size() + box_0[1].size() + box_0[2].size();
			Grid full;
			full.clear();
			for (int i = 0; i < hh; i++) {
				vector<char> v;
				if (i == 0 || i == hh - 1) {
					v.push_back('+');
					v.push_back(' ');
					for (int k = 0; k < box_0[0].size(); k++) {
						v.push_back('-');
					}
					v.push_back(' ');
					v.push_back('|');
					v.push_back(' ');
					for (int k = 0; k < box_1[0].size(); k++) {
						v.push_back('-');
					}
					v.push_back(' ');
					v.push_back('|');
					v.push_back(' ');
					for (int k = 0; k < box_2[0].size(); k++) {
						v.push_back('-');
					}
					v.push_back(' ');
					v.push_back('+');
				}
				else if (i == 1) {
					v.push_back('|');
					v.push_back(' ');
					for (int k = 0; k < box_0[0].size(); k++) {
						v.push_back(' ');
					}
					v.push_back(' ');
					v.push_back('|');
					v.push_back(' ');
					for (int k = 0; k < box_1[0].size(); k++) {
						v.push_back(' ');
					}
					v.push_back(' ');
					v.push_back('|');
					v.push_back(' ');
					for (int k = 0; k < box_2[0].size(); k++) {
						v.push_back(' ');
					}
					v.push_back(' ');
					v.push_back('|');
				}
				else if (i == hh - 2) {
					v.push_back('|');
					v.push_back(' ');
					for (int k = 0; k < box_0[0].size(); k++) {
						v.push_back(' ');
					}
					v.push_back(' ');
					v.push_back('|');
					v.push_back(' ');
					for (int k = 0; k < box_1[0].size(); k++) {
						v.push_back(' ');
					}
					v.push_back(' ');
					v.push_back('|');
					v.push_back(' ');
					for (int k = 0; k < box_2[0].size(); k++) {
						v.push_back(' ');
					}
					v.push_back(' ');
					v.push_back('|');
				}
				else {
					v.push_back('|');
					v.push_back(' ');
					for (int k = 0; k < box_0[0].size(); k++) {
						if (i - 2 >= box_0.size()) {
							v.push_back(' ');
						}
						else {
							v.push_back(box_0[i - 2][k]);
						}
					}
					v.push_back(' ');
					v.push_back('|');
					v.push_back(' ');
					for (int k = 0; k < box_1[0].size(); k++) {
						if (i - 2 >= box_1.size()) {
							v.push_back(' ');
						}
						else {
							v.push_back(box_1[i - 2][k]);
						}
					}
					v.push_back(' ');
					v.push_back('|');
					v.push_back(' ');
					for (int k = 0; k < box_2[0].size(); k++) {
						if (i - 2 >= box_2.size()) {
							v.push_back(' ');
						}
						else {
							v.push_back(box_2[i - 2][k]);
						}
					}
					v.push_back(' ');
					v.push_back('|');
				}
				full.push_back(v);
				v.clear();
			}

			//show all
			for (const auto& row : full) {
				for (const auto& element : row) {
					cout << element;
				}
				cout << endl;
			}
			full.clear();

			//print info
			for (int i = 0; i < 3; i++) {
				cout << "(";
				for (int j = 0; j < 3; j++)
				{
					cout << info[i][j];
					if (j != 2)
						cout << ", ";
				}
				cout << ") ";
			}
			cout << endl;

			//compute win or lose
			if (((type[1] == type[2])||(type[0] == type[2])||(type[0] == type[1]))
				&& area.size() <= 2) {
				bet *= 3;
				cout << "Jackpot! 2 Similar Shapes AND 2 Equal Screen Areas" << endl;
				cout << "Congratulations! You win 3 times your bet : " << bet << endl;
			}
			else if (type[0]==type[1] && type[0]==type[2] && type[1]==type[2]) {
				bet *= 2;
				cout << "Three similar shapes" << endl;
				cout << "Congratulations! You win 2 times your bet: " << bet << endl;
			}
			else if (reel[1]->screenArea() > (reel[0]->screenArea() + reel[2]->screenArea())) {
				bet = bet;
				cout << "Middle > Left + Right, in Screen Areas" << endl;
				cout << "Congratulations! You win your bet: " << bet << endl;
			}
			else if (type[0] == type[2]) {
				bet = 0;
				cout << "Lucky this time!" << endl;
				cout << "You don't win, you don't lose, you are safe!" << endl;
			}
			else {
				bet *= -1;
				cout << "Oh No!" << endl;
				cout << "You lose your bet" << endl;

			}

			currentToken += bet;
			cout << "You now have " << currentToken << " tokens" << endl;

			reel[0].release();
			reel[1].release();
			reel[2].release();
			area.clear();

		}
			
	}
	
}
