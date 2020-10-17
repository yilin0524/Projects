/**
* @author  Yilin Li 40083064
*/
#include <iostream>
#include <vector>
#include <math.h>
using namespace std;
#include "Rhombus.h"

Rhombus::Rhombus(int di, string n, string d) :Shape(n, d) {
	if (di % 2 == 0) {
		di = di + 1;
	}
	diagonal = di;
	height = di;
	width = di;
	id = Shape::identity;
}

int Rhombus::identity_getter() const {
	return id;
}

string Rhombus::name_getter() const {
	return Shape::name;
}

string Rhombus::description_getter() const {
	return Shape::description;
}


double Rhombus::area() const {
	return (diagonal * diagonal) / 2.0;
}

double Rhombus::perimeter() const {
	return 2.0 * sqrt(2) * diagonal;
}

int Rhombus::screenArea() const {
	int n;
	n = diagonal / 2;
	return 2 * n * (n + 1) + 1;
}

int Rhombus::screenPerimeter() const {
	return 2 * (diagonal - 1);
}

Grid Rhombus::draw(char fChar, char bChar) const {
	Grid grid;
	int h = height / 2;
	int right = width / 2;
	int left = right;
	for (int i = 0; i <= h; i++) {
		vector<char> v;
		for (int k = 0; k < width; k++) {
			if (k >= left && k <= right) {
				v.push_back(fChar);
			}
			else {
				v.push_back(bChar);
			}
		}
		grid.push_back(v);
		left--;
		right++;
	}
	left = 1;
	right = height - 2;
	for (int i = 0; i < h; i++) {
		vector<char> v;
		for (int k = 0; k < width; k++) {
			if (k >= left && k <= right) {
				v.push_back(fChar);
			}
			else {
				v.push_back(bChar);
			}
		}
		grid.push_back(v);
		left++;
		right--;
	}
	return grid;
}

int Rhombus::getHeight() const {
	return height;
}

int Rhombus::getWidth() const {
	return width;
}

ostream& operator<<(ostream& out, Rhombus& rhombus) {
	out << rhombus.toString();
	return out;
}
