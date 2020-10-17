/**
* @author  Yilin Li 40083064
*/

#ifndef SHAPE_H
#define SHAPE_H

#include <iostream>
#include <vector>
#include <string>
using namespace std;

using Grid = vector<vector<char>>; // a vector of vectors of chars

class Shape {
protected:
	static int identity;
	string name;
	string description;

public:
	Shape(string n, string d);

	virtual int identity_getter() const = 0;
	virtual string name_getter() const = 0;
	virtual string description_getter() const = 0;
	string name_setter(string n);
	string description_setter(string d);

	string toString() const;

	virtual double area() const = 0;
	virtual double perimeter() const = 0;
	virtual int screenArea() const = 0;
	virtual int screenPerimeter() const = 0;
	
	virtual Grid draw(char fChar = '*', char bChar = ' ') const = 0;

	virtual int getHeight() const = 0;
	virtual int getWidth() const = 0;

};


#endif //SHAPE_H
#pragma once