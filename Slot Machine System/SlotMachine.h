/**
* @author  Yilin Li 40083064
*/

#ifndef SLOTMACHINE_H
#define SLOTMACHINE_H

#include <iostream>
#include <array>
using namespace std;
#include "Shape.h"

class SlotMachine {
	array<unique_ptr<Shape>, 3> reel{};
	//void make_shapes();// Step 6-19
	//void make_shape(int k); // Steps 7-18
	//void display();// Step 23
public:
	SlotMachine() = default;
	SlotMachine(const SlotMachine&) = delete; // copy ctor
	SlotMachine(SlotMachine&&) = delete; // move ctor
	SlotMachine& operator=(const SlotMachine&) = delete; // copy assignment
	SlotMachine& operator=(SlotMachine&&) = delete; // move assignment
	virtual ~SlotMachine() = default;
	void run(int t=10);
};

#endif //SLOTMACHINE_H
#pragma once