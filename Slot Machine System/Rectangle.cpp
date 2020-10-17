/**
* @author  Yilin Li 40083064
*/
#include <iostream>
#include <vector>
using namespace std;
#include "Rectangle.h"

Rectangle::Rectangle(int w, int h, string n, string d) :Shape(n, d) {
	width = w;
	height = h;
	id = Shape::identity;
}

int Rectangle::identity_getter() const {
	return id;
}

string Rectangle::name_getter() const {
	return Shape::name;
}

string Rectangle::description_getter() const {
	return Shape::description;
}

double Rectangle::area() const {
	return height * width;
}

double Rectangle::perimeter() const {
	return 2.0 * (height + width);
}

int Rectangle::screenArea() const {
	return height * width;
}

int Rectangle::screenPerimeter() const {
	return 2 * (height + width) - 4;
}

Grid Rectangle::draw(char fChar, char bChar) const {
	Grid grid;
	for (int i = 0; i < height; i++) {
		vector<char> v;
		for (int j = 0; j < width; j++) {
			v.push_back(fChar);
		}
		grid.push_back(v);
	}
	return grid;
}

int Rectangle::getHeight() const {
	return height;
}

int Rectangle::getWidth() const {
	return width;
}

ostream& operator<<(ostream& out, Rectangle& rectangle) {
	out << rectangle.toString();
	return out;
}