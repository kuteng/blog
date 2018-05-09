深度学习
================================

.. toctree::
   :maxdepth: 3

总结
^^^^^^^^^^^^^^^^^

代码
^^^^^^^^^^^^^^^^^
numpy
#################
基本语法
::::::::::::::::
- 创建数组 ::

    vector = numpy.array([1., "2", 3, 4]);

  获取数组的大小 ::

    print(vector.shape);

  获取数组中，数据的类型（numpy要求数组元素的类型需要统一） ::

    print(vector.dtype);

  打印对象的类型(type) ::

    print(type(vector));

  打印对象 ::

    print(vector);

  读取数据文件(文件以逗号为数据分割符，第一行为和header)，生成二维数组 ::

    world_alcohol = numpy.genfromtxt("datas/world_alcohol.txt", delimiter=",", dtype=str, skip_header=1);

  提取数组中的一个元素 ::

    uruguay_other_1986 = world_alcohol[1,4];

  切片：获取数组中的 `一段` 元素 ::

    # 行（范围)：这里是一维数组。
    print(vector[0:3]);
    # 列：其中单独的冒号表示所有列。
    print(world_alcohol[:,1]);
    # 列（范围）
    print(world_alcohol[:,0:2]);
    # 行（范围），列（范围）
    print(world_alcohol[0:9,0:2]);

计算
::::::::::::::::
- 判断 ::

    vector = numpy.array([1., 2, 3, 4]);
    # 判断数组中，各元素是否等于10
    print(vector == 10);
    matrix = numpy.array([
        [5, 10, 15],
        [20, 25, 30],
        [35, 40, 45]
        ]);
    # 判断数组中，各元素是否等于15
    print(matrix == 15);

  输出结果是 ::

    [False False False False]
    [[False False  True]
     [False False False]
     [False False False]]

  使用判断结果，过滤原数据集 ::

    second_column_25 = (matrix[:, 1] == 25);
    print(second_column_25);
    print(matrix[second_column_25, :]);
    print(matrix[:, second_column_25]);

  输出结果是 ::

    [False  True False]
    [[20 25 30]]
    [[10]
     [25]
     [40]]

- 数字运算 ::

    vector = numpy.array([5, 10, 3 * 5, 4 * 5]);
    equal_to_ten = (vector % 10 == 0);
    print(equal_to_ten);
    print(vector[equal_to_ten]);

  输出结果是 ::

    [False  True False  True]
    [10 20]

  二维数组 ::

    second_column_25 = (matrix[:, 1] % 10 == 0);
    print(second_column_25);
    print(matrix[second_column_25, :]);
    print(matrix[:, second_column_25]);

  输出结果是 ::

    [ True False  True]
    [[ 5 10 15]
     [35 40 45]]
    [[ 5 15]
     [20 30]
     [35 45]]

- 逻辑运算 ::

    vector = numpy.array([1, 2, 3, 4]);
    vector = vector * 5;
    equal_to_ten_or_five = (vector == 10) | (vector == 5);
    print(equal_to_ten_or_five);
    # 根据结果，对数组部分元素进行再赋值
    vector[equal_to_ten_or_five] = 50;
    print(vector);

  输出结果是 ::

    [ True  True False False]
    [50 50 15 20]
