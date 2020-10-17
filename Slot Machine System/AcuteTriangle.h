/**
* @author  Yilin Li 40083064
*/

#ifndef ACUTETRIANGLE_H
#define ACUTETRIANGLE_H

#include <iostream>
using namespace std;
#include "Triangle.h"

class AcuteTriangle : public Triangle {
protected:
	int base;
	int height;
	int id;
public:
	AcuteTriangle(int b, string n= "Wedge", string d= "All acute angels");

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

	friend ostream& operator<< (ostream& out, AcuteTriangle& acuteTriangle);
};


#endif //ACUTETRIANGLE_H
#pragma once
