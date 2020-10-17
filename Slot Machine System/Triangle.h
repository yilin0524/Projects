/**
* @author  Yilin Li 40083064
*/

#ifndef TRIANGLE_H
#define TRIANGLE_H

#include <iostream>
using namespace std;
#include "Shape.h"

class Triangle : public Shape {
public:
	int base;
	int height;
	Triangle(int b, int h, string n="", string d="");
};

#endif //TRIANGLE_H
#pragma once