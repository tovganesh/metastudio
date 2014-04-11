#!/bin/sh

javaexe=`which -a java`
#javaexe=`find / -name java`

realjava="java"

for ja in $javaexe
do
  javaver=`$ja -version 2>&1 | sed -n 's/^java version \"\(.*\)\"/\1/p'`
  jvmajor=`echo $javaver | sed -n 's/^\([0-9]*\).*$/\1/p'`
  jvminor=`echo $javaver | sed -n 's/^[0-9]*\.\([0-9]*\).*$/\1/p'`

  if [ $jvmajor -ge "1" ]; then
    if [ $jvminor -ge "6" ]; then
       realjava=$ja
    fi
  fi   
done

echo $realjava
echo `dirname "$0"`

cd `dirname "$0"`
$realjava -jar MeTA.jar
cd -

