rm -f meta-bin.tar.gz

mkdir meta
mkdir meta/bin
mkdir meta/lib
mkdir meta/lib/ext
mkdir meta/scripts
mkdir meta/widgets
mkdir meta/help

cp ~/meta/*.txt meta/

cp ~/meta/bin/MeTA.jar meta/bin/
cp ~/meta/bin/README.TXT meta/bin/
cp ~/meta/installer/win32/icon.ico meta/bin/
cp ~/meta/installer/linx86/icon.png meta/bin/
cp ~/meta/installer/linx86/meta.sh meta/bin/

cp ~/meta/lib/*.jar meta/lib/
cp ~/meta/lib/*.txt meta/lib/
cp -r ~/meta/lib/ext/* meta/lib/ext/
cp -r ~/meta/lib/linux meta/lib/
cp -r ~/meta/lib/macosx meta/lib/
cp -r ~/meta/lib/solaris meta/lib/
cp -r ~/meta/lib/windows meta/lib/

cp ~/meta/scripts/* meta/scripts/
cp ~/meta/widgets/* meta/widgets/
cp ~/meta/help/*.jar meta/help/

tar jcvf meta-bin.tar.bz meta/

rm -f -r meta/
