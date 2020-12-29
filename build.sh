#!/bin/bash
# build BlackSpider On Ubuntu20.04,tested on https://labs.play-with-docker.com/
# $ docker run -it -p 8848:8848 ubuntu:20.04
# (in docker container)$ apt-get update && apt-get install -y wget && sh -c "$(wget https://raw.githubusercontent.com/liux-pro/BlackSpider/master/build.sh -O -)"
apt-get update
apt-get install -y build-essential libz-dev zlib1g-dev
apt-get install -y git wget curl
# graalvm native-image  building request libfreetype-dev ,which not mention on document
apt-get install -y libfreetype-dev

# language
apt-get install -y language-pack-zh-hans
locale-gen zh_CN.UTF-8
export LC_ALL=zh_CN.UTF-8

rm -rf BlackSpiderBuild
mkdir BlackSpiderBuild
cd BlackSpiderBuild || exit
mkdir graal
mkdir mvn

#set up graalvm and native image component
cd graal || exit
# code from  https://gist.github.com/lukechilds/a83e1d7127b78fef38c2914c4ececc3c
# get latest version from github
graal_version=$(curl --silent "https://api.github.com/repos/graalvm/graalvm-ce-dev-builds/releases/latest" |  grep '"tag_name":' |     sed -E 's/.*"([^"]+)".*/\1/')
wget https://github.com/graalvm/graalvm-ce-dev-builds/releases/download/"$graal_version"/graalvm-ce-java11-linux-amd64-dev.tar.gz
tar -xzf graalvm-ce-java11-linux-amd64-dev.tar.gz
JAVA_HOME=$(pwd)/graalvm-ce-java11-21.0.0-dev
export JAVA_HOME
export PATH=$JAVA_HOME/bin:$PATH
gu instal native-image
cd ..

#set up mvn
cd mvn || exit
wget https://downloads.apache.org/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz
tar -xzf apache-maven-3.6.3-bin.tar.gz
MAVEN_HOME=$(pwd)/apache-maven-3.6.3
export MAVEN_HOME
export PATH=$MAVEN_HOME/bin:$PATH
cd ..

apt-get install -y git
git clone https://github.com/liux-pro/BlackSpider.git
cd BlackSpider || exit
git checkout master

mvn clean
mvn -B package --file pom.xml -Dmaven.test.skip=true -Pwindows
mvn clean
mvn -B package --file pom.xml -Dmaven.test.skip=true -Plinux
mvn clean
mvn -B package --file pom.xml -Dmaven.test.skip=true -Pmac

apt-get install -y python3
echo "a http file service started at http://:8848"
echo "check result on your browser"
python3 -m http.server 8848