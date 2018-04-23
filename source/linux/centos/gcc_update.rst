升级GCC
======================================

默认情况下，CentOS 7.2预装的gcc版本是4.8.x，通过执行命令 gcc -v 可以看到，一般情况下这个版本的编译器已经满足需要了，但是某些特殊的时候为了支持C++更高的特性，需要对gcc编译器的版本进行升级，比如安装最新的Mariadb 10的时候，就需要使用高版本的gcc，具体升级过程如下：

- 首先去官网下载gcc的高版本安装包，镜像列表是：https://gcc.gnu.org/mirrors.html 进入后选择其中的镜像站，下载gcc即可
- 然后下在依赖项：GMP 4.2+, MPFR 2.4.0+ and MPC 0.8.0+。

  - gmp：http://ftp.gnu.org/gnu/gmp/
  - mpfr(GNU镜像)：http://ftp.gnu.org/gnu/mpfr/ 或者官网:http://www.mpfr.org/mpfr-current/
  - mpc：http://ftp.gnu.org/gnu/mpc/

- 首先安装gmp，命令如下： ::

    tar -xvzf gmp-6.1.2.tar.gz
    cd gmp-6.1.2/
    mkdir temp
    cd temp/
    ../configure --prefix=/usr/local/gmp-6.1.2
    make
    make install

 这样就安装好了，注意：编译时建议指定安装位置，以便后面加载依赖，这里是/usr/local下

- 然后安装mpfr，命令如下: ::

    tar -xvzf mpfr-4.0.1.tar.gz
    cd mpfr-4.0.1/
    mkdir temp
    cd temp/
    ../configure --prefix=/usr/local/mpfr-4.0.1 --with-gmp=/usr/local/gmp-6.1.2
    make
    make install

- 到这里mpfr安装完毕，并且必须添加--with-gmp导入gmp依赖，如果不加这个参数也会安装成功，但是后面安装GCC会报一个内部依赖的错误，如果这里不加会很麻烦，然后再安装mpc，命令如下： ::

    tar -xvzf mpc-1.1.0.tar.gz
    cd mpc-1.1.0/
    mkdir temp
    cd temp/
    ../configure --prefix=/usr/local/mpc-1.1.0 --with-gmp=/usr/local/gmp-6.1.2 --with-mpfr=/usr/local/mpfr-4.0.1
    make
    make install

- 同样一定要加上依赖的参数，现在mpc也安装完毕，然后执行 vim /etc/profile 编辑环境变量配置文件，直接在文件最后添加一行下面的变量： ::

    export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/local/mpc-1.1.0/lib:/usr/local/gmp-6.1.2/lib:/usr/local/mpfr-4.0.1/lib

- 上面的路径要和实际安装时编译的路径一致，保存并退出后，执行 source /etc/profile 使环境变量生效。
- 需要先安装这个 ::

    yum install gcc-c++

- 最后就可以开始安装gcc了，安装过程如下： ::

    tar -jxvf gcc-4.9.3.tar.bz2
    cd gcc-4.9.3/
    mkdir output
    cd output/
    ../configure --disable-multilib --enable-languages=c,c++ --with-gmp=/usr/local/gmp-6.1.2 --with-mpfr=/usr/local/mpfr-4.0.1 --with-mpc=/usr/local/mpc-1.1.0

- 然后开始编译并且安装： ::

    make -j4
    make install

- make过程时间非常长，根据计算机配置不同，时间有所差别，一般来说半个小时到一个多小时都是正常的，因为gcc编译器相对来说还是比较庞大的一个项目的，安装成功之后执行 gcc -v 或者 gcc --version 查看新的版本信息

----------

或者这样

- 需要先安装这个 ::

    yum install gcc-c++

- 运行这个 ::

    ./contrib/download_prerequisites

- 然后 ::

    mkdir output
    cd output/
    ../configure --enable-checking=release --enable-languages=c,c++ --disable-multilib
    make -j23
    make install
