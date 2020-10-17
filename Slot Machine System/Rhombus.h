/**
* @author  Yilin Li 40083064
*/

#ifndef RHOMBUS_H
#define RHOMBUS_H

#include <iostream>
using namespace std;
#include "Shape.h"

class Rhombus : public Shape {
protected:
	int id;
	int diagonal;
	int width;
	int height;
public:
	Rhombus(int di, string n= "Diamond", string d= "Parallelogram with equal sides");

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

	friend ostream& operator<< (ostream& out, Rhombus& rhombus);
};

#endif //RHOMBUS_H
#pragma once