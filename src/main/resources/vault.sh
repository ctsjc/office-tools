#!/bin/sh
if [ "$1" == "" ]; then
  echo "-d <decrypt> -e <encrypt>"
fi
while [ "$1" != "" ]; do
    case $1 in
        -e )          java -jar /j/sw/tools/vault-1.0-SNAPSHOT-jar-with-dependencies.jar -f /j/pass.txt -k pass -e $2
                                ;;
        -d )          java -jar /j/sw/tools/vault-1.0-SNAPSHOT-jar-with-dependencies.jar -f /j/pass.txt -k pass -d $2
                                ;;
        -h | --help )           usage
                                exit
                                ;;
        * )                     echo "-d <decrypt> -e <encrypt>"
                                exit 1
    esac
    shift
done
