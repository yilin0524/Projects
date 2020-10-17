/**
* @author  Yilin Li 40083064
*/
#include <iostream>
using namespace std;
#include "Triangle.h"

Triangle::Triangle(int b, int h, string n, string d) : Shape(n, d) {
	base = b;
	height = h;
}