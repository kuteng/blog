#!/usr/bin/python
# -*- coding: UTF-8 -*-

import numpy;

vector = numpy.array([1., 2, 3, 4]);
print(vector.shape);
print(vector.dtype);
vector = numpy.array([1., "2", 3, 4]);
print(vector.shape);
print(vector.dtype);

world_alcohol = numpy.genfromtxt("datas/world_alcohol.txt", delimiter=",", dtype=str, skip_header=1);
print(type(world_alcohol));
print(world_alcohol);

uruguay_other_1986 = world_alcohol[1,4];
print(uruguay_other_1986);
third_country = world_alcohol[2, 2];
print(third_country);

print("\n---切片---");
print(vector);
print(vector[0:3]);


print("\n---列---");
# 单独的冒号表示所有列。
print(world_alcohol[:,1]);

print("\n---列（范围）---");
print(world_alcohol[:,0:2]);

print("\n---行（范围），列（范围）---");
print(world_alcohol[0:9,0:2]);

print("\n---计算---");
vector = numpy.array([1., 2, 3, 4]);
print(vector == 10);
matrix = numpy.array([
    [5, 10, 15],
    [20, 25, 30],
    [35, 40, 45]
    ]);
print(matrix == 15);

print("");
vector = numpy.array([5, 10, 3 * 5, 4 * 5]);
equal_to_ten = (vector % 10 == 0);
print(equal_to_ten);
print(vector[equal_to_ten]);

second_column_25 = (matrix[:, 1] == 25);
print("");
print(second_column_25);
print(matrix[second_column_25, :]);
print(matrix[:, second_column_25]);

print("");
second_column_25 = (matrix[:, 1] % 10 == 0);
print(second_column_25);
print(matrix[second_column_25, :]);
print(matrix[:, second_column_25]);

print("");
vector = numpy.array([1, 2, 3, 4]);
vector = vector * 5;
equal_to_ten_or_five = (vector == 10) | (vector == 5);
print(equal_to_ten_or_five);
vector[equal_to_ten_or_five] = 50;
print(vector);

print("\n---关于类型---");
vector = numpy.array(["1", "2", "3"]);
print(vector);
print(vector.dtype);
vector = vector.astype(float);
print(vector);
print(vector.dtype);
