# Assignment 2 for the Systems Programming course at BGU 
the product of Lior and Emi

Grade: 100

Java-written code

## The task is:
Programming a system to mimic the use of a centralized processing pool.

Users (students) can ask to train and test their models utilizing the university's resources. A university has servers that can theoretically process data and train machine learning models.

This task put our knowledge of Java generics, concurrency, and synchronization to the test. The ideas of Micro Services, Promise-based programming with Futures, Design by Contract, and Test Driven Development have all been used in our work.

## Implementation:
The program uses threads to process student requests to train-test their models, which are then published to conferences. It parses an input file with the various objects( Micro-services) we are to create( Students, Models, University resources such as CPUs and GPUs). Every trained model, the total amount of time( in ticks) that each GPU CPU worked, and the number of data batches processed are all detailed in the output file that the program generates at the end of the timer.
