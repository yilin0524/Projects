/**
* @author  Yilin Li 40083064
*/
#include <iostream>
#include <vector>
#include <math.h>
using namespace std;
#include "AcuteTriangle.h"

AcuteTriangle::AcuteTriangle(int b, string n, string d) :Triangle((b % 2 == 0) ? (b + 1):b , (b % 2 == 0) ? ((b + 2)/2) : ((b+1)/2), n, d) {
	if (b % 2 == 0) {
		b = b + 1;
	}
	base = b;
	height = (b + 1) / 2;
	id = Shape::identity;
}

int AcuteTriangle::identity_getter() const {
	return id;
}

string AcuteTriangle::name_getter() const {
	return Shape::name;
}

string AcuteTriangle::description_getter() const {
	return Shape::description;
}

double AcuteTriangle::area() const {
	return (height * base) / 2.0;
}

double AcuteTriangle::perimeter() const {
	return base + sqrt( base * base + 4.0 * height * height);
}

int AcuteTriangle::screenArea() const {
	return height * height;
}

int AcuteTriangle::screenPerimeter() const {
	return 4 * (height - 1);
}

Grid AcuteTriangle::draw(char fChar, char bChar) const {
	Grid grid;
	int right = (base + 1) / 2;
	int left = right;
	for (int i = 0; i < height; i++) {
		vector<char> v;
		for (int k = 0; k <= base; k++) {
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
	return grid;
}

int AcuteTriangle::getHeight() const {
	return height;
}

int AcuteTriangle::getWidth() const {
	return base;
}

ostream& operator<< (ostream& out, AcuteTriangle& acuteTriangle) {
	out << acuteTriangle.toString();
	return out;
}