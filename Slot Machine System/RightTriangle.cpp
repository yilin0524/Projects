/**
* @author  Yilin Li 40083064
*/
#include <iostream>
#include <vector>
#include <math.h>
using namespace std;
#include "RightTriangle.h"

RightTriangle::RightTriangle(int b, string n, string d) :Triangle(b, b, n, d) {
	base = b;
	height = b;
	id = Shape::identity;
}

int RightTriangle::identity_getter() const {
	return id;
}

string RightTriangle::name_getter() const {
	return Shape::name;
}

string RightTriangle::description_getter() const {
	return Shape::description;
}

double RightTriangle::area() const {
	return (height * base) / 2.0;
}

double RightTriangle::perimeter() const {
	return (2.0 + sqrt(2)) * height;
}

int RightTriangle::screenArea() const {
	return height * (height + 1) / 2;
}

int RightTriangle::screenPerimeter() const {
	return 3 * (height - 1);
}

Grid RightTriangle::draw(char fChar, char bChar) const {
	Grid grid;
	for (int i = 0; i < height; i++) {
		vector<char> v;
		for (int j = 0; j < height; j++) {
			if (j <= i) {
				v.push_back(fChar);
			}
			else {
				v.push_back(bChar);
			}
		}
		grid.push_back(v);
	}
	return grid;
}

int RightTriangle::getHeight() const {
	return height;
}

int RightTriangle::getWidth() const {
	return base;
}

ostream& operator<< (ostream& out, RightTriangle& rightTriangle) {
	out << rightTriangle.toString();
	return out;
}