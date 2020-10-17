/**
* @author  Yilin Li 40083064
*/
#include <iostream>
#include <vector>
#include <iomanip>
#include <sstream>
#include <typeinfo>
using namespace std;
#include "Shape.h"

int Shape::identity = 0;

Shape::Shape(string n, string d) {
	name = n;
	description = d;
	identity = identity + 1;
}

string Shape::name_setter(string n) {
	name = n;
}

string Shape::description_setter(string d) {
	description = d;
}

string Shape::toString() const {
	ostringstream out;
	out << "Shape Information\n";
	out << "-----------------\n";
	out << "id:            " << identity_getter() << endl;
	out << "Shape name:    " << Shape::name << endl;
	out << "Description:   " << Shape::description << endl;
	out << "B. box width:  " << getWidth() << endl;
	out << "B. box height: " << getHeight() << endl;
	out << "Scr area:      " << screenArea() << endl;
	out << "Geo area:      " << fixed << setprecision(2) << area() << endl;
	out << "Scr perimeter: " << screenPerimeter() << endl;
	out << "Geo perimeter: " << fixed << setprecision(2) << perimeter() << endl;
	out << "Static type:   " << typeid(this).name() << endl;
	out << "Dynamic type:  " << typeid(*this).name() << endl;
	return out.str();
}
