

here=$(dirname $(readlink -f $0))

for file in $(find . -name 'old*' -prune -o -type f -name '*.v' -print)
do
    (
        echo =============================== $file
        dir=`dirname $file`
        cd $dir
        filename=`basename $file`
        $here/clean.sh 
    ) &
done

