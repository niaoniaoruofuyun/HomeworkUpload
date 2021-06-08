# HomeworkUpload
基于WebDav的作业提交系统  
本项目实例地址：[0bug.cn](https://www.0bug.cn)，如果需要测试文件上传功能，请上传以“网站测试.txt”为名称的文本文件。
## 编译
#### 环境
</br>jdk1.8、Tomcat9.0
#### 依赖包
</br>commons-codec-1.6.jar</br>commons-codec-1.6-sources.jar</br>commons-fileupload-1.4.jar</br>commons-io-2.8.0.jar</br>commons-logging-1.1.1.jar
</br>commons-logging-1.1.1-sources.jar</br>httpclient-4.5.10.jar</br>httpclient-4.5.10-sources.jar</br>httpcore-4.4.7.jar</br>httpcore-4.4.7-sources.jar
</br>httpmime-4.5.jar</br>httpmime-4.5-sources.jar</br>sardine-5.9.jar</br>sardine-5.9-sources.jar
## 安装
</br>1. 下载war包到Tomcat目录下webapp文件夹内，启动Tomcat，war包文件将释放到对应文件夹。
</br>2. 所有文件会释放到webapp文件夹下以war包名称命名的文件夹内
</br>3. [webapp](./src/main/webapp)里的css和images文件夹需要移动到Tomcat目录下的webapp文件夹内，index.html和task.html需要移动到webapp目录下ROOT文件夹内，File.properties移动到Tomcat目录下的conf文件夹
## 配置
</br>你需要修改Tomcat目录下的conf文件夹内的[File.properties](./src/main/webapp/File.properties)配置文件的内容

>#Uri: WebDav地址(http或者https)  
Uri=https://xxx.xxx:xxx/dav/  
#UserName: WebDav用户名  
UserName=User123  
#PassWord: WebDav密码  
PassWord=PassWord123  
#FileName: 要检查文件名(以,,,三个英文逗号作为分割)  
#FileFolder: 要保存的文件夹(如果识别到上传的文件名中包含FileName，则会上传到对应的FileFolder，如果不需要分类可以放空)  
FileName=作业,,,  
FileFolder=作业,,,  
#是否创建“其他”这个文件夹(如果不符合上面的文件分类，True: 则会放入"其他"文件夹，False: 放入WebDav地址默认目录)  
OtherFolder=True
  
</br>其中WebDav可以选择自己实现(比如[clouderve](https://cloudreve.org/))，或者使用[坚果云](https://www.jianguoyun.com/)(WebDav账号创建在账号信息的安全选项里)
## 进度
- [x] 基本实现基于WebDav协议实现文件上传
- [ ] 为了减少硬盘占用，文件上传到服务器直接通过WebDav传输到云盘，服务器不保存文件，有文件保存失败的风险
- [ ] 为了让用户感受到快速上传，网页报错只考虑了文件上传到服务器部分和网络问题，如果在传输到云盘过程中发生问题，用户不可知
>可能错误场景:用户上传文件后，想再次上传同名称文件覆盖旧文件，但此时第一次上传的文件还处在由服务器传给云盘的过程中，文件为锁状态不可更改.(此项问题已经可以网页报错)  
目前解决方案暂定为:完成云盘传输任务后再返回信息到前端,返回信息速度取决于服务器网速,可以根据自己的需求选择不同方案.
- [ ] 名单统计功能未完成
