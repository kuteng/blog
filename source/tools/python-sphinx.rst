Python Sphinx
==========================
Sphinx是一个工具，她能够轻易地创建智慧和优雅的文档，她是出自Georg Brandl之手，在BSD许可证下授权。

Sphinx 使用 reStructuredText 作为她的标记语言，她的优点大部分是来自于reStructuredText 以及reStructuredText的解析和转换工具（套件）Docutils的强大以及简单明了。

参考网页
^^^^^^^^^^^^^^^^^^^^^
- http://www.pythondoc.com/sphinx/index.html

常用命令
^^^^^^^^^^^^^^^^^^^^^

代码块
:::::::::::::::::::::

可标记 **代码块** 的几种方法
###############################
- 方法一： ::

    下面是代码块代码： ``这里是代码`` 。

- 方法二： ::

    下面是代码块代码： ::

      这里是代码

    代码块已结束。

- 方法三： ::

    ..code-block:: java

      这里是Java代码

    代码块结束。

- 方法四：引入代码文件 ::

    ..literalinclude:: <filepath>

code-block 的辅助标记
##############################
- ``linenos`` ：代码块中显示行号。
- ``linenothreshold: <number>`` ：代码行数超过 `number` 时，代码块中显示行号。
- ``emphasize-lines: <num1>, <num1>-<num2>`` ：高亮标记某些行。

用法示例： ::

  .. code-block:: python
     :linenos:
     :linenothreshold: 5
     :emphasize-lines: 1, 3-5

     def some_function():
         interesting = False
         print 'This line is highlighted.'
         print 'This one is not...'
         print '...but this one is.'

literalinclude 的辅助标记
##############################
- ``linenos`` ：代码块中显示行号。
- ``linenothreshold: <number>`` ：代码行数超过 `number` 时，代码块中显示行号。
- ``emphasize-lines: <num1>, <num1>-<num2>`` ：高亮标记某些行。
- ``language`` ：代码的语言，有助于代码高亮。
- ``encoding`` ：表示文件的编码。
- ``pyobject`` ：该指令还支持仅包含文件的一部分。如果它是Python模块，您可以使用pyobject选项选择要包含的类，函数或方法。
- ``lines`` ：我们可以通过给出这个选项来准确指定要包含的行。

用法示例： ::

  .. literalinclude:: example.rb
     :language: ruby
     :emphasize-lines: 12,15-18
     :linenos:
     :encoding: latin-1

  .. literalinclude:: example.py
     :pyobject: Timer.start

  .. literalinclude:: example.py
     :lines: 1,3,5-10,20-

图片引用
::::::::::::::::::::::
示例代码： ::

  这里是插入图片的位置： |image_tag|

  .. |image_tag| image:: /imagepath/imagefolder/image_file_name
     :width: 100%

