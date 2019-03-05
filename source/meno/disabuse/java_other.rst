Java的解惑备忘
========================

占位

--------------

读嵌套字的内容： ::

    ServerSocket serverSocket = new ServerSocket(Constants.CONCURRENT_PORT);
    Socket clientSocket = serverSocket.accept();
    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    String line = in.readLine();
    ....

--------------

待续
