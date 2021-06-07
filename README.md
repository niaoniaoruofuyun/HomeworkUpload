# HomeworkUpload
基于WebDav的作业提交系统
## 编译
#### 环境
</br>jdk1.8、Tomcat9.0
#### 依赖包
</br>commons-codec-1.6.jar</br>commons-codec-1.6-sources.jar</br>commons-fileupload-1.4.jar</br>commons-io-2.8.0.jar</br>commons-logging-1.1.1.jar
</br>commons-logging-1.1.1-sources.jar</br>httpclient-4.5.10.jar</br>httpclient-4.5.10-sources.jar</br>httpcore-4.4.7.jar</br>httpcore-4.4.7-sources.jar
</br>httpmime-4.5.jar</br>httpmime-4.5-sources.jar</br>sardine-5.9.jar</br>sardine-5.9-sources.jar
## 安装
</br>1.下载war包到Tomcat目录下webapp文件夹内，启动Tomcat，war包文件将释放到对应文件夹。
</br>2.所有文件会释放到webapp文件夹下以war包名称命名的文件夹内
</br>3.[webapp](./src/main/webapp)里的css和images文件夹需要移动到Tomcat目录下的webapp文件夹内，index.html和task.html需要移动到webapp目录下ROOT文件夹内，File.properties移动到Tomcat目录下的conf文件夹
