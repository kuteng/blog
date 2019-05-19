红黑树
==================
二叉树
^^^^^^^^^^^^^^^^^
- 将N个数据插入到一颗二叉树中，来减少迭代的次数，加快查询。
- *不偏二叉树* 查值的速度会比 *偏二叉树* 更快。
- 二叉树合理的高度是 log2(N)。

遍历顺序
:::::::::::::::::
|01_red_black_about_tree_binary_tree|
|02_red_black_about_tree_binary_tree|

如上图

前序遍历
  遍历结果分别为： ``ABC`` 、 ``ABCDEFGHK`` 。

  应用场景：可以用来实现目录结构的显示。

中序遍历
  遍历结果分别为： ``BAC`` 、 ``BDCAEHCKF`` 。

  应用场景：一颗树，被中序遍历之后，就是value从小到大的排列，当然也可以用来做表达式树，在编译器底层实现的时候用户可以实现基本的加减乘除，比如 a*b+c。

后序遍历
  遍历结果分别为： ``BCA`` 、 ``DCBHKGFEA`` 。

  应用场景：可以用来实现计算目录内的文件占用的数据大小。

红黑树
^^^^^^^^^^^^^^^^^^
红黑树是一种特殊的二叉树，是一种 **自平衡二叉树** 。在每次插入新的数据的时候，红黑树会进行 **变色** 和 **旋转** 来使生成的树不会出现偏的情况

红黑树的原则
:::::::::::::::::
#. 每个节点或者是黑色，或者是红色。
#. 根节点是黑色。
#. 每个叶子节点（NIL）是黑色。 [注意：这里叶子节点，是指为空(NIL或NULL)的叶子节点！]
#. 如果一个节点是红色的，则它的子节点必须是黑色的，也就是说不可能出现两个连续的红色节点，不过两个连续的黑色节点是可能出现的
#. 从任意一个节点到该节点的子孙节点的所有路径上包含相同数目的黑节点。

红黑树的构建
:::::::::::::::::
基本原则：

  - 红黑树在插入数据的时候，会先遍历数据应该插入到哪个位置，插入的位置肯定在底部，不可能在中间突然插入一个值。
  - **插入的数据一定是红色的** （因为要遵守红黑树的 *第五条规则* ，如果有一条分支增加了一个黑色节点，就会打破该规则）
  - 插入之后，为了满足 *规则4* ，就需要用到 **换色** 与 **左旋** 、 **右旋** 的操作了。

红黑树的换色与旋转
::::::::::::::::::::
换色
  换色，其实就是红变黑，黑变红，只需要让某个对象的属性改变就可以了，没什么好说的。

左旋
  |01_red_black_about_levogyration|
  |02_red_black_about_levogyration|
  |03_red_black_about_levogyration|

右旋
  |01_red_black_about_dextrorotation|
  |02_red_black_about_dextrorotation|
  |03_red_black_about_dextrorotation|

数据插入时，一般会遇到四种情况：
  注意：插入的节点固定为红色。

  #. 如果是根节点：直接插入，然后将根节点转为黑色。
  #. 插入节点的父亲为黑色：插入就完事了，不用做任何的改动
  #. 插入节点的父亲为红色，叔叔节点（插入节点的爷爷的另一个子节点）的颜色也是红色

     直接把叔叔和爸爸变成黑色，然后把爷爷变成和自己一样的红色，继续迭代（因为这样可能会出现爷爷和太爷爷的都是红色的情况，那么就要继续判断是哪种情况）

  #. 插入节点的父亲为红色，叔叔节点节点为黑色（这种情况最麻烦，因为需要再做一次判断）

     - 阶段一：如果插入节点、父亲节点、爷爷节点不在一条直线上，通过对 *父亲节点* 的左旋或右旋，将他们调整到一条直线上。然后阶段二
     - 阶段二：如果插入节点、父亲节点、爷爷节点在一条直线上，通过对 *爷爷节点* 的左旋或右旋，将 *爷爷节点* 调整到下一层，让 *父亲节点* 代替 *爷爷节点* 原来的位置，最后将 *父亲节点* 与 *爷爷节点* 的颜色互换。（如果阶段二是由阶段一转化而来，那么上面说的的 *父亲节点* 应该改为 *插入节点* ）。

示例分析
:::::::::::::::::
构建红黑树，先后插入数字：90, 70, 120, 74, 78, 84, 80, 82

插入 ``90``
  这是第一种情况

  |01_red_black_about_build_example|

插入 ``70`` 和 ``120`` 
  这是第二种情况

  |02_red_black_about_build_example|

插入 ``74``
  这是第三种情况

  |03_red_black_about_build_example|

插入 ``78``
  这是第四种情况的第二阶段

  |04_red_black_about_build_example|

  此次变换之后，将树的层次由五层变为了四层（不算NULL节点）。

插入 ``84``
  这是第三种情况

  |05_red_black_about_build_example|

插入 ``80``
  这是第四种情况的第一阶段， **首先需要将其转换到第二阶段** 。

  |06_red_black_about_build_example|

  此次变换之后，将树的层次由五层变为了四层（不算NULL节点）。

插入 ``82``
  这是第三种情况，但是处理完成后发现 ``74`` 、 ``80`` 节点都为红色，所有出现了 *第四种情况的第一阶段* 。

  |07_red_black_about_build_example|

  此次变换之后，不但将树的层次由五层变为了四层（不算NULL节点），而且使得本树的左右平衡了。

综上所有又可以总结出下面的 **结论** ：
  - 情况三解决后，可能使得树左右平衡。
  - 情况四解决后，会尽量维持树的层次。

  注意： **上面的结论不一定正确，因为是我个人的总结**

.. |01_red_black_about_tree_binary_tree| image:: /images/special_subject/data_structure/001_red_black_about_tree_binary_tree.jpeg
   :width: 80%
.. |02_red_black_about_tree_binary_tree| image:: /images/special_subject/data_structure/001_red_black_about_tree_binary_tree_02.png
   :width: 80%
.. |01_red_black_about_levogyration| image:: /images/special_subject/data_structure/001_red_black_about_levogyration_01.png
   :width: 25%
.. |02_red_black_about_levogyration| image:: /images/special_subject/data_structure/001_red_black_about_levogyration_02.png
   :width: 25%
.. |03_red_black_about_levogyration| image:: /images/special_subject/data_structure/001_red_black_about_levogyration_03.png
   :width: 25%
.. |01_red_black_about_dextrorotation| image:: /images/special_subject/data_structure/001_red_black_about_dextrorotation_01.png
   :width: 25%
.. |02_red_black_about_dextrorotation| image:: /images/special_subject/data_structure/001_red_black_about_dextrorotation_02.png
   :width: 25%
.. |03_red_black_about_dextrorotation| image:: /images/special_subject/data_structure/001_red_black_about_dextrorotation_03.png
   :width: 25%
.. |01_red_black_about_build_example| image:: /images/special_subject/data_structure/001_red_black_about_build_example_01.png
   :width: 100%
.. |02_red_black_about_build_example| image:: /images/special_subject/data_structure/001_red_black_about_build_example_02.png
   :width: 100%
.. |03_red_black_about_build_example| image:: /images/special_subject/data_structure/001_red_black_about_build_example_03.png
   :width: 100%
.. |04_red_black_about_build_example| image:: /images/special_subject/data_structure/001_red_black_about_build_example_04.png
   :width: 100%
.. |05_red_black_about_build_example| image:: /images/special_subject/data_structure/001_red_black_about_build_example_05.png
   :width: 100%
.. |06_red_black_about_build_example| image:: /images/special_subject/data_structure/001_red_black_about_build_example_06.png
   :width: 100%
.. |07_red_black_about_build_example| image:: /images/special_subject/data_structure/001_red_black_about_build_example_07.png
   :width: 100%
