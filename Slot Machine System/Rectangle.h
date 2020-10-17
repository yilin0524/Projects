/**
* @author  Yilin Li 40083064
*/

#ifndef RECTANGLE_H
#define RECTANGLE_H

#include <iostream>
using namespace std;
#include "Shape.h"

class Rectangle : public Shape {
private:
	int id;
	int width;
	int height;
public:
	Rectangle(int w, int h, string n= "Rectangle", string d= "Four right angles");

	int identity_getter() const override;
	string name_getter() const override;
	string description_getter() const override;

	double area() const override;
	double perimeter() const override;
	int screenArea() const override;
	int screenPerimeter() const override;
	
	Grid draw(char fChar = '*', char bChar = ' ')const override;

	int getHeight() const override;
	int getWidth() const override;

	friend ostream& operator<< (ostream& out, Rectangle& rectangle);
};

#endif //RECTANGLE_H
#pragma once