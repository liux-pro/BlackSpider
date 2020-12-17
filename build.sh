#!/bin/bash
# build BlackSpider On Ubuntu20.04
# $ apt-get update && apt-get install -y wget && sh -c "$(wget https://legend-tech.com/build.sh -O -)"
apt-get update
apt-get install -y build-essential libz-dev zlib1g-dev
apt-get install -y git wget
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
wget https://github.com/graalvm/graalvm-ce-dev-builds/releases/download/21.0.0-dev-20201215_0301/graalvm-ce-java11-linux-amd64-dev.tar.gz
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
git checkout native

mvn -B package --file pom.xml -Dmaven.test.skip=true

apt-get install -y python3
echo "a http file service started at http://:8848"
echo "check result on your browser"
python3 -m http.server 8848