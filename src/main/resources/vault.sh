#!/bin/sh
if [ "$1" == "" ]; then
  echo "-d <decrypt> -e <encrypt> -ef <encrypt file content on console>"
fi
while [ "$1" != "" ]; do
    case $1 in
        -e )          java -jar /j/sw/tools/vault-1.0-SNAPSHOT-jar-with-dependencies.jar -f /j/pass.txt -k key -e $2
                                ;;
        -d )          java -jar /j/sw/tools/vault-1.0-SNAPSHOT-jar-with-dependencies.jar -f /j/pass.txt -k key -d $2
                                ;;
        -ef )          java -jar /j/sw/tools/vault-1.0-SNAPSHOT-jar-with-dependencies.jar -f /j/pass.txt -k key -ef $2
                                ;;
        -h | --help )           usage
                                exit
                                ;;
        * )                     echo "-d <decrypt> -e <encrypt>"
                                exit 1
    esac
    shift
done
