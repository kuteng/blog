Vim
========================

使用 Vim Bundle
^^^^^^^^^^^^^^^^^^^
- 使用Git下载：

  ``git clone https://github.com/gmarik/Vundle.vim.git ~/.vim/bundle/Vundle.vim``

- 修改 ``.vimrc`` 。修改示例如下： ::

    filetype off
    syntax on
    set rtp+=~/.vim/bundle/Vundle.vim
    call vundle#begin()
    " 加载插件
    Plugin 'VundleVim/Vundle.vim'
    Plugin 'scrooloose/nerdtree'
    Plugin 'vim-airline/vim-airline'
    Plugin 'vim-airline/vim-airline-themes'
    Plugin 'michaelHL/awesome-vim-colorschemes'
    Plugin 'Tagbar'
    Plugin 'Tabular'
    call vundle#end()
    filetype plugin indent on     " required

- 插件初始化：打开vim在底行模式下输入 ``BundleInstall`` 。
- 如果还需要其他插件，可以使用 ``BundleSearch`` 进行在线查找插件。

常用命令
^^^^^^^^^^^^^^
设置折叠
:::::::::::::::
::

    set foldmethod=indent
    set fdm=indent

- ``manual``           手工定义折叠
- ``indent``           更多的缩进表示更高级别的折叠
- ``expr``             用表达式来定义折叠
- ``syntax``           用语法高亮来定义折叠
- ``diff``             对没有更改的文本进行折叠
- ``marker``           对文中的标志折叠


设置tab为四个空格
:::::::::::::::::::
::

    set ts=4
    set noexpandtab
    %retab!

使用标签页
:::::::::::::::::::
- 使用标签页打开多个文件 ::

    vim -p [fileName1] [fileName2] ...

- 启动标签页： ::

    :tabe[dit]
    :tabnew [fileName]

- 切换标签页 ::

    :tabn[ext]、:tabN[ext]、:tabp[revious]
    :tabn[ext] {count}
    gt、gT
    :tabfir[st]、:tabl[ast]

- 列出所有标签页 ::

    :tabs

- 关闭标签页 ::

    :tabc[lose][!]  关闭当前标签页
    :tabc[lose][!]  {count} 关闭第N个标签页。
    :tabo[nly][!]   关闭其他标签页。


查找
::::::::::
-   在所有行中查找 字符串 出现的次数 ::

        :%s/字符串/&/gn

-   在m和n行之间查找 字符串 出现的次数 ::

        :m,ns/字符串/&/gn

替换
::::::::::::
在命令最后加c，则表示每次替换前需要手动确认

-   替换当前行第一个 vivian 为 sky ::

        :s/vivian/sky/

-   替换当前行所有 vivian 为 sky ::

        :s/vivian/sky/g
        :s/vivian/sky/gc

-   替换每一行的第一个 vivian 为 sky ::

        :%s/vivian/sky/

-   替换每一行中所有 vivian 为 sky ::

        :%s/vivian/sky/g
        :%s/vivian/sky/gc

-   替换第 n 行开始到最后一行中每一行的第一个 vivian 为 sky (n 为数字，若 n 为 .，表示从当前行开始到最后一行) ::

        :n,$s/vivian/sky/

-   替换第 n 行开始到最后一行中每一行所有 vivian 为 sky (n 为数字，若 n 为 .，表示从当前行开始到最后一行) ::

        :n,$s/vivian/sky/g
        :n,$s/vivian/sky/gc

-   让首字母大写 ::

        :%s/^[a-z]/\U&/gc

快捷命令
::::::::::::::
-   将选中范围内的字母变为小写 `gu` ，变为大写 `gU`

相关的系统命令
::::::::::::::::
-   `lsof -c  vim` 看看vim都打开了哪些文件。

无法选中文字
::::::::::::::::::::::::
-   ``set mouse=v``

帮助文档
^^^^^^^^^^^^^^^^^
- 中文帮助文档的下载路径：https://sourceforge.net/projects/vimcdoc/files/vimcdoc/
- 中文PDF文档的下载路径：https://sourceforge.net/projects/vimcdoc/files/pdf-manual/
- 中文PDF文档的下载路径：http://vimcdoc.sourceforge.net/

