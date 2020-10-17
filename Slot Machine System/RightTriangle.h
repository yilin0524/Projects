/**
* @author  Yilin Li 40083064
*/

#ifndef RIGHTTRIANGLE_H
#define RIGHTTRIANGLE_H

#include <iostream>
using namespace std;
#include "Triangle.h"

class RightTriangle : public Triangle {
protected:
	int base;
	int height;
	int id;
public:
	RightTriangle(int b, string n = "Ladder", string d = "One right and two acute angles");

	int identity_getter() const override;
	string name_getter() const override;
	string description_getter() const override;

	double area() const override;
	double perimeter() const override;
	int screenArea() const override;
	int screenPerimeter() const override;

	Grid draw(char fChar = '*', char bChar = ' ') const override;

	int getHeight() const override;
	int getWidth() const override;

	friend ostream& operator<< (ostream& out, RightTriangle& rightTriangle);
};

#endif //RIGHTTRIANGLE_H
#pragma once