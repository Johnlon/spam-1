
set -e
for file in *.asm
do
    echo =============================================================
    echo $file
    echo =============================================================
    ./gradlew run --args=$file
done
