/**
* @author  Yilin Li 40083064
*/
#include <iostream>
using namespace std;
#include "Shape.h"
#include "Triangle.h"
#include "Rhombus.h"
#include "Rectangle.h"
#include "AcuteTriangle.h"
#include "RightTriangle.h"
#include "SlotMachine.h"

ostream& operator<< (ostream& sout, const Grid& grid);
ostream& operator<<(ostream& out, Shape& shape);
void poly_draw_shape_by_ref(const Shape& shape, char foreground, char background );
void poly_draw_shape_by_ptr(const Shape* pShape, char foreground , char background );
void task1();
void task2();

ostream& operator<< (ostream& sout, const Grid& grid)
{
	for (const auto& row : grid) // for each row vector in the grid
	{
		for (const auto& element : row) // for each element (char) in the row vector
		{
			sout << element;
		}
		sout << endl; // line break
	}
	return sout;
}
ostream& operator<<(ostream& out, Shape& shape) {
	out << shape.toString();
	return out;
}

void poly_draw_shape_by_ref(const Shape& shape, char foreground = '*', char background = ' ')
{
	Grid shape_box = shape.draw(foreground, background);
	cout << shape_box << endl;
}
void poly_draw_shape_by_ptr(const Shape* pShape, char foreground = '*', char background = ' ')
{
	Grid shape_box = pShape->draw(foreground, background);
	cout << shape_box << endl;
}

void task1() {
	Rectangle rect{ 5, 7 };
	//cout << rect.identity_getter() <<endl;
	cout << rect.toString() << endl;
	// or equivalently
	// cout << rect << endl;

	Rhombus ace{ 16, "Ace", "Ace of diamond" };
	//cout << ace.identity_getter() << endl;
	// cout << ace.toString() << endl;
	// or, equivalently:
	cout << ace << endl;
	
	AcuteTriangle at{ 17 };
	cout << at << endl;
	//equivalently:
	//Shape *atptr = &at;
	//cout << *atptr << endl;
	//Shape &atref = at;
	//cout << atref << endl;

	RightTriangle rt{ 10, "Carpenter's square" };
	cout << rt << endl;


	Grid aceBox = ace.draw('+', '.');
	cout << aceBox << endl;

	Grid rectBox = rect.draw();
	cout << rectBox << endl;
	
	Grid atBox = at.draw('^');
	cout << atBox << endl;

	Grid rtBox = rt.draw('-');
	cout << rtBox << endl;

	rtBox = rt.draw('\\', 'o');
	cout << rtBox << endl;

	aceBox = ace.draw('o');
	cout << aceBox << endl;

	
	cout << "----------------Draw through Shape&----------------------"<<endl;
	Rectangle rect_ref{ 5, 7 };
	Rhombus ace_ref{ 16, "Ace", "Ace of diamond" };
	AcuteTriangle at_ref{ 17 };
	RightTriangle rt_ref{ 10, "Carpenter’s square" };
	poly_draw_shape_by_ref(rect_ref);
	poly_draw_shape_by_ref(ace_ref, '+', '.');
	poly_draw_shape_by_ref(at_ref, '^');
	poly_draw_shape_by_ref(rt_ref, '-');


	cout << "---------------Draw through unique_ptr--------------------"<<endl;
	unique_ptr<Shape> rectShape{ new Rectangle{5, 7} };
	Grid rectBox_ptr = rectShape->draw();
	cout << rectBox_ptr << endl;
	std::unique_ptr<Shape> aceShape{ new Rhombus{16, "Ace", "Ace of diamond"} };
	Grid aceBox_ptr = aceShape->draw('+', '.');
	cout << aceBox_ptr << endl;

	std::unique_ptr<Shape> atShape{ new AcuteTriangle(17) };
	Grid atBox_ptr = atShape->draw('^');
	cout << atBox_ptr << endl;
	std::unique_ptr<Shape> rtShape{ new RightTriangle(10, "Carpenter’s square") };
	Grid rtBox_ptr = rtShape->draw('-');
	cout << rtBox_ptr << endl;
	// no need to delete any resources
}

void task2() {
	SlotMachine slot_machine;// create a slot machine object
	slot_machine.run();// run our slot machine until the player decides to stop, or until the player runs out of tokens
}

int main()
{
	task1();
	task2();
	return 0;
}
