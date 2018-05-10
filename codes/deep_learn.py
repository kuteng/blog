#!/usr/bin/python
# -*- coding: UTF-8 -*-

import numpy;

vector = numpy.array([1., 2, 3, 4]);
matrix = numpy.array([
    [5, 10, 15],
    [20, 25, 30],
    [35, 40, 45]
]);
world_alcohol = numpy.genfromtxt("world_alcohol.txt", delimiter=",", dtype=str, skip_header=1);

# qiujizhi
print("\n---求极值---");
print(vector.min());
print(vector.max());

print("\n---求和---");
# 对行求和
print(matrix.sum(axis=1));
# 对列求和
print(matrix.sum(axis=0));

print("\n--- 初始化 和 转换 ----");
# 生成一个连续的一维数组（向量）
a = numpy.arange(15);
print(a);
# 将一维数组（向量）转换为二维数组（矩阵）: 指定行数/列数
b = a.reshape(3, 5);
print(b);

print("\n--- 矩阵的属性 ---");
# 打印矩阵的size。
print(b.shape);
# 打印矩阵的维度。
print(b.ndim);
# 打印矩阵的类型。
print(b.dtype);
print(b.dtype.name);
# 打印元素数量。
print(a.size);
print(b.size);

print("\n--- 矩阵初始化 ---");
# 初始化一个3行4列，元素全为0的矩阵。默认类型为float
print(numpy.zeros((3, 4)));
# 生成一个元素都为1，类型为int的三维矩阵。
print(numpy.ones((2, 3, 4), dtype=numpy.int32));
# 生成一个元素有序的三维矩阵。元素在[10, 30)之间且间隔为5的一组数。
print(numpy.arange(10, 30, 5));
# 类似于上面
print(numpy.arange(0, 2, 0.3));

# 随即生成矩阵。默认是(-1, 1)之上的值。
print(numpy.random.random((2, 3)));

# 生成一个向量，size为100，元素内容是[0, 2*PI]之间的间隔相同的100个数字。
print(numpy.linspace(0, 2 * numpy.pi, 100));

# 矩阵计算。
print(numpy.sin(numpy.linspace(0, 6.24, 100)));

print("\n--- 矩阵计算 ---");
a = numpy.array([20, 30, 40, 50]);
b = numpy.arange(4);
# 矩阵向减。两矩阵的size必须相同？
c = a - b;
print(c);
# 矩阵与数字运算。 `**` 表示成方操作。  
print(c - 1);
print(c * 2);
print(c ** 2);
print(c < 30);
